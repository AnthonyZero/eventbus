package com.github.likavn.eventbus.demo.controller;

import com.github.likavn.eventbus.core.api.MsgSender;
import com.github.likavn.eventbus.core.base.MsgListenerContainer;
import com.github.likavn.eventbus.core.utils.Assert;
import com.github.likavn.eventbus.demo.constant.MsgConstant;
import com.github.likavn.eventbus.demo.domain.R;
import com.github.likavn.eventbus.demo.domain.TMsg;
import com.github.likavn.eventbus.demo.domain.TestBody;
import com.github.likavn.eventbus.demo.listener.DemoMsgDelayListener;
import com.github.likavn.eventbus.demo.service.BsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author likavn
 * @date 2024/1/15
 **/
@Slf4j
@RestController
@RequestMapping("/eventbus")
public class DemoController {

    @Lazy
    @Resource
    private MsgSender msgSender;

    @Lazy
    @Resource
    private MsgListenerContainer msgListenerContainer;

    @Resource
    private BsHelper bsHelper;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 测试消息
     */
    @PostMapping(value = "/trigger/{type}/{count}")
    public R<String> trigger(@PathVariable("type") Integer type,
                             @PathVariable("count") Long count, @PathParam("delayTime") Long delayTime, @RequestBody String content) {
        try {
            long l = System.currentTimeMillis();
            log.info("发送消息数量count={}条,msg={}", count, content);
            Assert.notEmpty(content, "msg不能为空");
            CountDownLatch latch = new CountDownLatch(Math.toIntExact(count));
            for (int i = 0; i < count; i++) {
                executorService.execute(() -> {
                    switch (type) {
                        case 1:
                            TestBody testBody = new TestBody();
                            testBody.setContent(content);
                            msgSender.send(testBody);
                            break;
                        case 2:
                            TMsg msg2 = new TMsg();
                            msg2.setContent(content);
                            msgSender.send(MsgConstant.DEMO_ANN_LISTENER, msg2);
                            break;
                        case 3:
                            TMsg msg3 = new TMsg();
                            msg3.setContent(content);
                            msgSender.sendDelayMessage(DemoMsgDelayListener.class, msg3, delayTime);
                            break;
                        case 4:
                            TMsg msg4 = new TMsg();
                            msg4.setContent(content);
                            msgSender.sendDelayMessage(MsgConstant.DEMO_ANN_DELAY_LISTENER, msg4, delayTime);
                            break;
                        default:
                            log.error("发送失败...");
                    }
                    latch.countDown();
                });
            }
            latch.await();
            String logStr = String.format("发送成功，耗时%s ms...", (System.currentTimeMillis() - l));
            log.info(logStr);
            return R.ok(logStr);
        } catch (Exception e) {
            log.error("DemoController.trigger", e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping(value = "/active")
    public R<Boolean> active() {
        return R.ok(msgListenerContainer.isActive());
    }

    @GetMapping(value = "/start")
    public R<Boolean> start() {
        try {
            msgListenerContainer.startup();
            return R.ok(Boolean.TRUE);
        } catch (Exception e) {
            log.error("DemoController.start", e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping(value = "/stop")
    public R<Boolean> stop() {
        try {
            msgListenerContainer.shutdown();
            return R.ok(Boolean.TRUE);
        } catch (Exception e) {
            log.error("DemoController.stop", e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping(value = "/reSendMessage")
    public R<Boolean> reSendMessage(@RequestParam("consumerId") Long consumerId) {
        bsHelper.reSendMessage(consumerId);
        return R.ok(Boolean.TRUE);
    }
}
