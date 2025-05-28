package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.service.FavoriteService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 收藏相关接口
 * 负责用户对商品的收藏、取消收藏、收藏状态检查、获取我的收藏列表等功能
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService; // 注入收藏业务逻辑

    /**
     * 添加收藏
     * @param shUserId 当前登录用户ID（从Cookie中获取）
     * @param favoriteModel 收藏对象（包含商品ID等信息）
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResultVo addFavorite(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestBody FavoriteModel favoriteModel){
        // 绑定当前用户ID
        favoriteModel.setUserId(Long.valueOf(shUserId));
        // 设置收藏时间
        favoriteModel.setCreateTime(new Date());
        // 调用service添加收藏
        if(favoriteService.addFavorite(favoriteModel)){
            // 成功返回收藏ID
            return ResultVo.success(favoriteModel.getId());
        }
        // 已经收藏过，返回特定错误
        return ResultVo.fail(ErrorMsg.FAVORITE_EXIT);
    }

    /**
     * 取消收藏
     * @param shUserId 当前登录用户ID
     * @param id 收藏记录ID
     * @return 操作结果
     */
    @GetMapping("/delete")
    public ResultVo deleteFavorite(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                       @RequestParam Long id){
        // 直接根据收藏ID删除
        if(favoriteService.deleteFavorite(id)){
            return ResultVo.success();
        }
        // 删除失败，返回系统错误
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 检查某商品是否已被当前用户收藏
     * @param shUserId 当前登录用户ID
     * @param idleId 商品ID
     * @return true/false
     */
    @GetMapping("/check")
    public ResultVo checkFavorite(@CookieValue("shUserId")
                                      @NotNull(message = "登录异常 请重新登录")
                                      @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestParam Long idleId){
        // 查询是否已收藏
        return ResultVo.success(favoriteService.isFavorite(Long.valueOf(shUserId),idleId));
    }

    /**
     * 获取当前用户的所有收藏
     * @param shUserId 当前登录用户ID
     * @return 收藏列表
     */
    @GetMapping("/my")
    public ResultVo getMyFavorite(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        // 查询所有收藏
        return ResultVo.success(favoriteService.getAllFavorite(Long.valueOf(shUserId)));
    }
}
