package com.xxz.loginhouduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * Find all users
     * @return list of SysUserEntity
     */
    @Select("SELECT id, login_name, email, password FROM sys_user")
    List<SysUserEntity> findAll();

    /**
     * Find user by email
     * @param email user's email
     * @return SysUserEntity
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUserEntity findByEmail(@Param("email") String email);

    /**
     * Find user by reset token
     * @param resetToken password reset token
     * @return SysUserEntity
     */
    @Select("SELECT * FROM sys_user WHERE reset_token = #{resetToken}")
    SysUserEntity findByResetToken(@Param("resetToken") String resetToken);

    /**
     * Update user's reset token and token expiration time by email
     * @param email user's email
     * @param resetToken generated reset token
     * @param resetTokenExpire formatted expiration time (yyyy-MM-dd HH:mm:ss)
     */
    @Update("UPDATE sys_user SET reset_token = #{resetToken}, reset_token_expire = #{resetTokenExpire} WHERE email = #{email}")
    void updateResetToken(@Param("email") String email, @Param("resetToken") String resetToken, @Param("resetTokenExpire") String resetTokenExpire);

    /**
     * Update password and clear reset token fields
     * @param resetToken password reset token
     * @param password new encrypted password
     */
    @Update("UPDATE sys_user SET password = #{password}, reset_token = NULL, reset_token_expire = NULL WHERE reset_token = #{resetToken}")
    void updatePassword(@Param("resetToken") String resetToken, @Param("password") String password);

    /**
     * Find user by login name
     * @param loginName user's login name
     * @return SysUserEntity
     */
    @Select("SELECT * FROM sys_user WHERE login_name = #{loginName}")
    SysUserEntity findByLoginName(@Param("loginName") String loginName);

}
