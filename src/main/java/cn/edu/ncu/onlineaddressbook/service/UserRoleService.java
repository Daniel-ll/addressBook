package cn.edu.ncu.onlineaddressbook.service;

import cn.edu.ncu.onlineaddressbook.bean.UserRole;
import cn.edu.ncu.onlineaddressbook.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: onlineAddressBook
 * @Author： LiuZedi
 * @Date： 2019/3/2 8:48
 */

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;


    /**
     * 插入用户-角色表
     *
     * @param username
     * @param rid
     * @return
     */
    public int insertUserRole(String username,int rid){
        return userRoleRepository.insertUserRole(username,rid);
    }

    /**
     * 根据用户账号查询用户的角色信息
     * @param username
     * @return
     */
    public UserRole getUserRoleByUsername(String username){
        return userRoleRepository.getUserRoleByUsername(username);
    }

}
