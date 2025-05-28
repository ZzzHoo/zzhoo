package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.MessageModel;
import com.second.hand.trading.server.service.MessageService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 留言/消息相关接口
 * 负责商品留言的发送、查询、删除等功能
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService; // 注入消息业务逻辑

    /**
     * 发送留言
     * @param shUserId 当前登录用户ID（从Cookie中获取）
     * @param messageModel 留言实体
     * @return 操作结果
     */
    @PostMapping("/send")
    public ResultVo sendMessage(@CookieValue("shUserId")
                                @NotNull(message = "登录异常 请重新登录")
                                @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                @RequestBody MessageModel messageModel){
        messageModel.setUserId(Long.valueOf(shUserId)); // 绑定用户ID
        messageModel.setCreateTime(new Date()); // 设置留言时间
        if(messageService.addMessage(messageModel)){
            return ResultVo.success(messageModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取单条留言详情
     * @param id 留言ID
     * @return 留言详情
     */
    @GetMapping("/info")
    public ResultVo getMessage(@RequestParam Long id){
        return ResultVo.success(messageService.getMessage(id));
    }

    /**
     * 获取某商品下的所有留言
     * @param idleId 商品ID
     * @return 留言列表
     */
    @GetMapping("/idle")
    public ResultVo getAllIdleMessage(@RequestParam Long idleId){
        return ResultVo.success(messageService.getAllIdleMessage(idleId));
    }

    /**
     * 获取当前用户的所有留言
     * @param shUserId 当前登录用户ID
     * @return 留言列表
     */
    @GetMapping("/my")
    public ResultVo getAllMyMessage(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(messageService.getAllMyMessage(Long.valueOf(shUserId)));
    }

    /**
     * 删除留言
     * @param shUserId 当前登录用户ID
     * @param id 留言ID
     * @return 操作结果
     */
    @GetMapping("/delete")
    public ResultVo deleteMessage(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestParam Long id){
        if(messageService.deleteMessage(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
