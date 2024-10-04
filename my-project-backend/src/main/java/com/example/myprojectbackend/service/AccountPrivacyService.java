package com.example.myprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myprojectbackend.entity.dto.AccountPrivacy;
import com.example.myprojectbackend.entity.vo.request.AccountPrivacyVO;

public interface AccountPrivacyService extends IService<AccountPrivacy> {
    void savePrivacy(int id, AccountPrivacyVO vo);
    AccountPrivacy accountPrivacy(int id);
}
