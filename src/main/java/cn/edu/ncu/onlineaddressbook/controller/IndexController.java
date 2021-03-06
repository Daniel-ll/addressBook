package cn.edu.ncu.onlineaddressbook.controller;

import cn.edu.ncu.onlineaddressbook.bean.User;
import cn.edu.ncu.onlineaddressbook.bean.UserInfo;
import cn.edu.ncu.onlineaddressbook.service.UserInfoRedisService;
import cn.edu.ncu.onlineaddressbook.service.UserInfoService;
import cn.edu.ncu.onlineaddressbook.service.UserRoleService;
import cn.edu.ncu.onlineaddressbook.service.UserService;
import cn.edu.ncu.onlineaddressbook.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;


/**
 * @Author： LiuZedi
 * @Date： 2019/2/27 8:44
 */

@Controller

public class IndexController {


    private final Logger logger= LoggerFactory.getLogger(this.getClass());


    @Autowired
    private IdUtil idUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserInfoRedisService userInfoRedisService;
    /**
     * 登录页面
     *
     * @return
     */
    @GetMapping("/login")
    public String loginPage(){
        return "login";

    }

    /**
     *
     * @return
     */
    @GetMapping("/index")
    public String index(){
        return "/admin/admin-index";
    }

    /**
     * 注册页面
     *
     * @return
     */
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    /**
     * 处理注册请求，并跳转
     *
     * @param userInfo
     * @param model
     * @return
     */
    @PostMapping("/re")
    public String  register(UserInfo userInfo,Model model){

        String status=idUtil.createId();
        userInfo.setUsername(status);
        userInfo.setPassword(new BCryptPasswordEncoder().encode(userInfo.getPassword()));
        User user=new User(userInfo.getUsername(),userInfo.getPassword(),userInfo.getName());
        if (!(userService.insertUser(user)==1&&userInfoService.insertUserInfo(userInfo)==1&&userRoleService.insertUserRole(status,1)==1)) {

            status = "注册失败      请再次注册！！！";
            logger.info(status);
            model.addAttribute("tip","以上是提示消息！！！");
        }else {
            userInfoRedisService.insertUserInfo(userInfo);
            logger.info("注册成功：{}", userInfo);
            model.addAttribute("tip","以上是你的账号，请务必记住！！！");
        }

        model.addAttribute("status",status);
        return "landing";
    }

    /**
     * 登录失败 显示原因
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/status")
    public String showStatus(HttpServletRequest request, HttpServletResponse response, Model model){

        String status= (String) request.getAttribute("status");
        request.removeAttribute("status");
        logger.info(status);
        model.addAttribute("status", status);
        model.addAttribute("tip","以上是你的账号登录错误信息！！！");

        return "landing";
    }

    /**
     * 管理员/用户登录
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/loginPage")
    public String LoginPage(HttpServletRequest request,Model model){

       // String username= (String) request.getAttribute("username");
        String username= (String) request.getSession().getAttribute("username");
       // request.removeAttribute("username");

        int disabledUsers=userService.getUserByEnabled(0).size();
        int lockedUsers=userService.getUserByLocked(0).size();
        int normalUsers=userService.getUsersByEnabledAndLocked(1,1).size();
        int allUsers=userService.getAllUsers().size();
        String name=userService.getUserOrAdmin(username).getName();
        userService.updateLoginTimeAndLoginTimesOfUser(new Timestamp(System.currentTimeMillis()),(userService.getUserOrAdmin(username).getLoginTimes()+1),username);
        model.addAttribute("username",username);
        model.addAttribute("name",name);
        model.addAttribute("disabledUsers",disabledUsers);
        model.addAttribute("lockedUsers",lockedUsers);
        model.addAttribute("normalUsers",normalUsers);
        model.addAttribute("allUsers",allUsers);

         if (userService.getAdminByUsername(username)!=null) {
             logger.info("管理员登录：："+username);
             return "/admin-index";

         }else {
             logger.info("用户登录：："+username);
             return "/user-index";
         }
    }


    /**
     *
     *
     * @return
     */
    @RequestMapping("/form")
    public String userForm(HttpServletRequest request, Model model){

        String username= (String) request.getSession().getAttribute("username");
        logger.info("用户登录：FORM："+username);
        UserInfo userInfo=userInfoRedisService.getUserByUsername(username);
        model.addAttribute("userInfo",userInfo);
        return "user-form";
    }

    /**
     * 错误页面
     *
     * @return
     */
    @GetMapping("/login-error")
    public String loginError(){
        return "login-error";
    }

    @GetMapping("/error")
    public String error(){
        return "/admin/admin-404";
    }

}
