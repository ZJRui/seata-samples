/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.samples.nacos;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * The type Application keeper.
 */
public class ApplicationKeeper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationKeeper.class);

    private final ReentrantLock LOCK = new ReentrantLock();
    private final Condition STOP = LOCK.newCondition();

    /**
     * Instantiates a new Application keeper.
     *
     * @param applicationContext the application context
     */
    public ApplicationKeeper(AbstractApplicationContext applicationContext) {
        addShutdownHook(applicationContext);
    }

    private void addShutdownHook(final AbstractApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    applicationContext.close();
                    LOGGER.info("ApplicationContext " + applicationContext + " is closed.");
                } catch (Exception e) {
                    LOGGER.error("Failed to close ApplicationContext", e);
                }

                // try {
                //     LOCK.lock();
                //     System.out.println("获取到lock锁");
                //     STOP.signal();
                // } finally {
                //     LOCK.unlock();
                // }
            }
        }));
    }

    /**
     * Keep.
     */
    public void keep() {
        synchronized (LOCK) {
            try {
                LOGGER.info("Application is keep running ... ");
                //之所以wait的原因是 系统中 没有其他 前台线程了，所以必须要有一个前台线程，否则程序就会退出
                //一般ReentrantLock的使用 是创建多个Conditon，然后使用Conditon对象的 wait和notify 方法，而不会直接使用ReentrantLock的wait
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("keep method end，结束keep方法");
        }
    }
}
