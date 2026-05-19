/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.*;
import com.sgu.admissor.dao.*;

/**
 *
 * @author Duc3m
 */
public class SGUAdmissorModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(AuthSession.class).in(Singleton.class);

        bind(UserDAO.class).in(Singleton.class);
        bind(ThiSinh2025DAO.class).in(Singleton.class);
        bind(NganhDAO.class).in(Singleton.class);
        bind(NguyenVongDAO.class).in(Singleton.class);
        bind(ChiTietTrungTuyenDAO.class).in(Singleton.class);
        bind(DiemThiDAO.class).in(Singleton.class);
        bind(DiemCongDAO.class).in(Singleton.class);
        bind(ToHopDAO.class).in(Singleton.class);
        bind(NganhToHopDAO.class).in(Singleton.class);
        bind(BangQuyDoiDAO.class).in(Singleton.class);

        bind(UserBUS.class).in(Singleton.class);
        bind(ThiSinh2025BUS.class).in(Singleton.class);
        bind(NganhBUS.class).in(Singleton.class);
        bind(NguyenVongBUS.class).in(Singleton.class);
        bind(ChiTietTrungTuyenBUS.class).in(Singleton.class);
        bind(DiemThiBUS.class).in(Singleton.class);
        bind(DiemCongBUS.class).in(Singleton.class);
        bind(ToHopBUS.class).in(Singleton.class);
        bind(NganhToHopBUS.class).in(Singleton.class);
        bind(BangQuyDoiBUS.class).in(Singleton.class);
        bind(ExcelImportBUS.class).in(Singleton.class);
    }
}
