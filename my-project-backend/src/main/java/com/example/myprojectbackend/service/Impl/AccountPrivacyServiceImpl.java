package com.example.myprojectbackend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.dto.AccountPrivacy;
import com.example.myprojectbackend.entity.vo.request.AccountPrivacyVO;
import com.example.myprojectbackend.mapper.AccountPrivacyMapper;
import com.example.myprojectbackend.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {
    @Override
    @Transactional
    public void savePrivacy(int id, AccountPrivacyVO vo) {
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.isValue();
        switch(vo.getType()) {
            case "phone" -> privacy.setPhone(status);
            case "email" -> privacy.setEmail(status);
            case "wechat" -> privacy.setWechat(status);
            case "qq" -> privacy.setQq(status);
            case "gender" -> privacy.setGender(status);
        }
        this.saveOrUpdate(privacy);

    }
    public AccountPrivacy accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }
}
