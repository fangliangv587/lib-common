package com.cenco.lib.common.log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/5/5.
 */
public class DiskLogStrategyTest {
    @Test
    public void addition_isCorrect() throws Exception {

        String mes = "I/libsample-util: xxxx";
        String mes1 = "2018.05.05 11:06:18.851 VERBOSE/libsample : mmmmmm\n";
        String subTagName = DiskLogStrategy.WriteHandler.getSubTagName(mes1);
        System.out.println(subTagName);

    }
}