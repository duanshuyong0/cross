package com.will.cross.controller;

import com.google.common.collect.Lists;
import com.will.cross.core.Result;
import com.will.cross.core.ResultGenerator;
import com.will.cross.configurer.WxApiConstant;
import com.will.cross.dto.SchedulePersonOrgRelateDTO;
import com.will.cross.dto.SysUserDTO;
import com.will.cross.model.*;
import com.will.cross.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* Created by PualrDwade on 2019/10/05.
*/
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController{
    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysOfficeService sysOfficeService;

    @Resource
    private  WxService wxService;

    @Resource
    private ScheduleWebRegisterService scheduleWebRegisterService;

    @Resource
    private SchedulePersonOrgRelateService schedulePersonOrgRelateService;

    @Autowired
    private Environment environment;

    @PostMapping
    public Result add(@RequestBody SysUser sysUser) {


        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setOpenid(getOpenId());
        sysUser.setDelFlag("0");
        // 判断当前用户是否存在，已存在则不创建，更新
        Condition query=new Condition(SysUser.class);

        query.createCriteria().andEqualTo("openid",getOpenId());

        List<SysUser> list = sysUserService.findByCondition(query);

        if(list.size()>0){
            sysUser.setId(list.get(0).getId());
            sysUserService.update(sysUser);
        } else{
            sysUserService.save(sysUser);
        }

        Condition queryt=new Condition(SysUser.class);

        queryt.createCriteria().andEqualTo("openid",getOpenId());

        List<SysUser> listt = sysUserService.findByCondition(queryt);

        SysOffice sysOffice =sysOfficeService.findById(sysUser.getOfficeId());

        //判断当前用户组织关系是否存在，已存在则不创建
        //查询用户，如果有用户，则返回，如果无用户，则创建用户；
        Condition querys=new Condition(SchedulePersonOrgRelate.class);

        querys.createCriteria().andEqualTo("personId",listt.get(0).getId()).andEqualTo("orgId",getMasterId());

        List<SchedulePersonOrgRelate> lists = schedulePersonOrgRelateService.findByCondition(querys);

        SchedulePersonOrgRelate m =new SchedulePersonOrgRelate();
        m.setId(UUID.randomUUID().toString());
        m.setPersonId(listt.get(0).getId());
        m.setOrgId(sysUser.getOfficeId());
        m.setOrgName(sysOffice.getName());
        m.setPersonName(sysUser.getName());
        m.setStatus("0");
        m.setIsAdmin("1");
        if(lists.size()<1){
            //添加人员组织关系
            schedulePersonOrgRelateService.save(m);
        } else{
            m.setId(lists.get(0).getId());
            schedulePersonOrgRelateService.update(m);
        }

        return ResultGenerator.genSuccessResult();

    }


    @RequestMapping(value = "/addWeb", method = RequestMethod.POST, produces = "application/json")
    public Result addWeb(@RequestBody SysUserVO sysUservo) {

        SysUser sysUser =new SysUser();
        for(PersonVO svo:sysUservo.getPerson()) {
            sysUser.setId(UUID.randomUUID().toString());
    //        sysUser.setOpenid(getOpenId());
            sysUser.setDelFlag("0");
            sysUser.setMobile(svo.getPhone());
            sysUser.setPhone(svo.getPhone());
            sysUser.setEmail(svo.getMail());
            sysUser.setName(svo.getName());
            sysUser.setOfficeId(getMasterId());
            // 判断当前用户是否存在，已存在则不创建，更新
            Condition query = new Condition(SysUser.class);

            query.createCriteria().andEqualTo("phone", svo.getPhone());

            List<SysUser> list = sysUserService.findByCondition(query);

            if (list.size() > 0) {
                sysUser.setId(list.get(0).getId());
                sysUserService.update(sysUser);
            } else {
                sysUserService.save(sysUser);
            }

            Condition queryt = new Condition(SysUser.class);

            queryt.createCriteria().andEqualTo("phone", svo.getPhone());

            List<SysUser> listt = sysUserService.findByCondition(queryt);

            SysOffice sysOffice = sysOfficeService.findById(sysUser.getOfficeId());

            //判断当前用户组织关系是否存在，已存在则不创建
            //查询用户，如果有用户，则返回，如果无用户，则创建用户；
            Condition querys = new Condition(SchedulePersonOrgRelate.class);

//            querys.createCriteria().andEqualTo("personId", listt.get(0).getId()).andEqualTo("orgId", getMasterId());
//
//            List<SchedulePersonOrgRelate> lists = schedulePersonOrgRelateService.findByCondition(querys);

            SchedulePersonOrgRelate m = new SchedulePersonOrgRelate();
            m.setId(UUID.randomUUID().toString());
            m.setPersonId(sysUser.getId());
            m.setOrgId(sysUser.getOfficeId());
            m.setOrgName(sysOffice.getName());
            m.setPersonName(sysUser.getName());
            m.setStatus("0");
            m.setIsAdmin("1");
            schedulePersonOrgRelateService.save(m);
            /*
            if (lists.size() < 1) {
                //添加人员组织关系

            } else {
                m.setId(lists.get(0).getId());
                schedulePersonOrgRelateService.update(m);
            }
               */
        }
        return ResultGenerator.genSuccessResult();

    }


    @RequestMapping(value = "/deleteWeb", method = RequestMethod.POST, produces = "application/json")
    public Result deleteWeb(@RequestBody SysUser sysUser) {
        //  sysUserService.deleteById(id);
        //查询用户，如果有用户，则返回，如果无用户，则创建用户；
        //
        //
        /*
        Condition query=new Condition(SchedulePersonOrgRelate.class);

        query.createCriteria().andEqualTo("personId",sysUser.getId()).andEqualTo("orgId",getMasterId());

        List<SchedulePersonOrgRelate> list = schedulePersonOrgRelateService.findByCondition(query);

        if(list.size()>0){
            for(SchedulePersonOrgRelate m:list)
            {
            }


        }
        */
        schedulePersonOrgRelateService.deleteById(sysUser.getId());

        return ResultGenerator.genSuccessResult();
    }



    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = "application/json")
    public Result delete(@RequestParam(required = true,value = "id")String id) {
      //  sysUserService.deleteById(id);
        //查询用户，如果有用户，则返回，如果无用户，则创建用户；
        Condition query=new Condition(SchedulePersonOrgRelate.class);

        query.createCriteria().andEqualTo("personId",id).andEqualTo("orgId",getMasterId());

        List<SchedulePersonOrgRelate> list = schedulePersonOrgRelateService.findByCondition(query);

        if(list.size()>0){
            for(SchedulePersonOrgRelate m:list)
            {
                schedulePersonOrgRelateService.deleteById(m.getId());
            }


        }


        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    public Result update(@RequestBody SchedulePersonOrgRelateDTO m) {

        SysUser sysUser=new SysUser();
        SchedulePersonOrgRelate schedulePersonOrgRelate=new SchedulePersonOrgRelate();

     //   BeanUtils.copyProperties(schedulePersonOrgRelate,m);
        schedulePersonOrgRelate.setId(m.getId());
        schedulePersonOrgRelate.setType(m.getType());
        schedulePersonOrgRelate.setPersonName(m.getName());


        BeanUtils.copyProperties(m,sysUser);
        sysUser.setId(m.getPersonId());
        sysUser.setPhone(m.getPhone());
        sysUser.setName(m.getName());
        sysUser.setPassword(m.getPassword());


        sysUserService.update(sysUser);
        schedulePersonOrgRelateService.update(schedulePersonOrgRelate);
        return ResultGenerator.genSuccessResult();
    }

    /*
    小程序端update
     */
    @RequestMapping(value = "/wxupdate", method = RequestMethod.POST, produces = "application/json")
    public Result wxupdate(@RequestBody SchedulePersonOrgRelateDTO m) {

        SysUser sysUser=new SysUser();


        SchedulePersonOrgRelate schedulePersonOrgRelate=new SchedulePersonOrgRelate();

        //   BeanUtils.copyProperties(schedulePersonOrgRelate,m);

        schedulePersonOrgRelate.setPersonName(m.getName());
        schedulePersonOrgRelate.setPersonId(m.getId());


        sysUser.setId(m.getId());
        sysUser.setPhone(m.getPhone());
        sysUser.setName(m.getName());

        schedulePersonOrgRelateService.updateNameBypersonId(schedulePersonOrgRelate);


        sysUserService.update(sysUser);
        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = "application/json")
    public Result detail(@RequestParam(required = true,value = "id")String id) {
        SysUser sysUser = sysUserService.findById(id);
        return ResultGenerator.genSuccessResult(sysUser);
    }


    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysUser> list = sysUserService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }



    /**
     * 根据客户端传过来的code从微信服务器获取appid和session_key，然后生成3rdkey返回给客户端，后续请求客户端传3rdkey来维护客户端登录态
     * @param wxCode	小程序登录时获取的code
     * @return
     */
    @ApiOperation(value = "获取sessionId", notes = "小用户允许登录后，使用code 换取 session_key api，将 code 换成 openid 和 session_key")
    @ApiImplicitParam(name = "code", value = "用户登录回调内容会带上 ", required = true, dataType = "String")
    @RequestMapping(value = "/openid", method = RequestMethod.GET, produces = "application/json")
    public Result createSssion(@RequestParam(required = true,value = "code")String wxCode){
        Map<String,Object> wxSessionMap = wxService.getWxSession(wxCode);

        if(null == wxSessionMap){
            return ResultGenerator.genFailResult("获取openid异常");
        }
        //获取异常
        if(wxSessionMap.containsKey(WxApiConstant.ERROR_CODE)){
            return ResultGenerator.genFailResult("获取openid异常");
        }
        String wxOpenId = (String)wxSessionMap.get("openid");
        String wxSessionKey = (String)wxSessionMap.get("session_key");
        System.out.println(wxSessionKey);
        Long expires = Long.valueOf(String.valueOf(wxSessionMap.get("expires_in")));

        //查询用户，如果有用户，则返回，如果无用户，则创建用户；
        Condition query=new Condition(SysUser.class);

        query.createCriteria().andEqualTo("openid",wxOpenId);

        List<SysUser> list = sysUserService.findByCondition(query);

        String userId="";
        if(list.size()>0){
            userId = list.get(0).getId();
        }
        else{
            //创建用户
            SysUser tmp=new SysUser();
            tmp.setOpenid(wxOpenId);
            tmp.setId(UUID.randomUUID().toString());
            tmp.setDelFlag("0");
            sysUserService.save(tmp);
            userId = tmp.getId();
        }

        String thirdSession = wxService.create3rdSession(wxOpenId, userId, expires);
        return ResultGenerator.genSuccessResult(thirdSession);
    }


    @ApiOperation(value = "获取组织用户", notes = "")
    @RequestMapping(value = "/listUser", method = RequestMethod.GET, produces = "application/json")
    public Result listUser(){

        List<SchedulePersonOrgRelateDTO> schedulePersonOrgRelateDTO= Lists.newArrayList();
        String orgId=getMasterId();

        Condition query=new Condition(SchedulePersonOrgRelate.class);
        query.createCriteria().andEqualTo("orgId",orgId);

        List<SchedulePersonOrgRelate> sys= schedulePersonOrgRelateService.findByCondition(query);

        //获取所有人员的信息
        List<String> personId = sys.stream().map(s -> s.getPersonId()).collect(Collectors.toList());

        personId = personId.stream().distinct().collect(Collectors.toList());

        String personIds = "";
        for (String ss : personId)
        {
            personIds += "'" +ss +"'" +  ",";
        }
        if(personIds.length()>1) {
            personIds = personIds.substring(0, personIds.length() - 1);
        }

        //   String shiftIds = shiftId.stream().collect(Collectors.joining(","));
        List<SysUser> user=new ArrayList<>();
        if(personIds.length()>0) {
            user = sysUserService.findByIds(personIds);
        }

        for(SchedulePersonOrgRelate sor:sys){
            SchedulePersonOrgRelateDTO m =new SchedulePersonOrgRelateDTO();
            BeanUtils.copyProperties( sor,m);

            List<SysUser> tt= user.stream().filter(e-> e.getId().equals(sor.getPersonId())).collect(Collectors.toList());

            if(tt.size()>0){
                m.setPhone(tt.get(0).getPhone());
                m.setEmail(tt.get(0).getEmail());
                m.setName(tt.get(0).getName());
                m.setOpenid(tt.get(0).getOpenid());
                if(getUserId().equals(tt.get(0).getId())){
                    m.setCurrentUserFlag("0");
                } else {
                    m.setCurrentUserFlag("1");
                }
            }

            schedulePersonOrgRelateDTO.add(m);
        }
        //  PageHelper.startPage(page, size);
//        Condition query=new Condition(SysUser.class);
//        query.createCriteria().andEqualTo("officeId",sysOffice.get(0).getId());

 //       List<SysUser> list = sysUserService.findByCondition(query);
        return ResultGenerator.genSuccessResult(schedulePersonOrgRelateDTO);
    }




    @ApiOperation(value = "获取组织用户", notes = "")
    @RequestMapping(value = "/listUserWeb", method = RequestMethod.GET, produces = "application/json")
    public Result listUserWeb(){

        List<SchedulePersonOrgRelateDTO> schedulePersonOrgRelateDTO= Lists.newArrayList();
        String orgId=getMasterId();

        Condition query=new Condition(SchedulePersonOrgRelate.class);
        query.createCriteria().andEqualTo("orgId",orgId);

        List<SchedulePersonOrgRelate> sys= schedulePersonOrgRelateService.findByCondition(query);

        //获取所有人员的信息
        List<String> personId = sys.stream().map(s -> s.getPersonId()).collect(Collectors.toList());

        personId = personId.stream().distinct().collect(Collectors.toList());

        String personIds = "";
        for (String ss : personId)
        {
            personIds += "'" +ss +"'" +  ",";
        }
        if(personIds.length()>1) {
            personIds = personIds.substring(0, personIds.length() - 1);
        }

        //   String shiftIds = shiftId.stream().collect(Collectors.joining(","));
        List<SysUser> user=new ArrayList<>();
        if(personIds.length()>0) {
            user = sysUserService.findByIds(personIds);
        }

        for(SchedulePersonOrgRelate sor:sys){
            SchedulePersonOrgRelateDTO m =new SchedulePersonOrgRelateDTO();

            List<SysUser> tt= user.stream().filter(e-> e.getId().equals(sor.getPersonId())).collect(Collectors.toList());

            if(tt.size()>0){
                BeanUtils.copyProperties( tt.get(0),m);
                BeanUtils.copyProperties( sor,m);

                m.setPhone(tt.get(0).getPhone());
                m.setEmail(tt.get(0).getEmail());
                m.setName(tt.get(0).getName());
                m.setOpenid(tt.get(0).getOpenid());
                if(getUserId().equals(tt.get(0).getId())){
                    m.setCurrentUserFlag("0");
                } else {
                    m.setCurrentUserFlag("1");
                }
            }

            schedulePersonOrgRelateDTO.add(m);
        }
        //  PageHelper.startPage(page, size);
//        Condition query=new Condition(SysUser.class);
//        query.createCriteria().andEqualTo("officeId",sysOffice.get(0).getId());

        //       List<SysUser> list = sysUserService.findByCondition(query);
        return ResultGenerator.genSuccessResult(schedulePersonOrgRelateDTO);
    }




    /**
     * 根据用户名、手机号、EMAIL与密码进行登录
     * @param
     * @return
     */
    @ApiOperation(value = "根据用户名密码进行登录", notes = "根据用户名、手机号、密码密码进行登录 生成session_key")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public Result login(@RequestBody SysUserDTO sysUserDto){


        Condition query=new Condition(SysUser.class);
        query.createCriteria().andEqualTo("phone",sysUserDto.getUsername()).andEqualTo("password",sysUserDto.getPassword());

        List<SysUser> list = sysUserService.findByCondition(query);


        if(list.size()<=0){
            return ResultGenerator.genFailResult("用户名或者密码错误");
        }

 //       System.out.println(wxSessionKey);
 //       Long expires = Long.valueOf(String.valueOf(wxSessionMap.get("expires_in")));
        Long expires=Long.valueOf(6000);
        String thirdSession = wxService.create3rdSession(list.get(0).getPhone(), list.get(0).getId(), expires);
        return ResultGenerator.genSuccessResult(thirdSession);
    }


    /**
     * 注册
     * @param sysUserDto
     * @return
     */
    @ApiOperation(value = "注册", notes = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public Result register(@RequestBody SysUserDTO sysUserDto){


        ScheduleWebRegister scheduleWebRegister=new ScheduleWebRegister();
        SysUser sysUser=new SysUser();

        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setPhone(sysUserDto.getPhone());
        sysUser.setPassword(sysUserDto.getPassword());
        sysUser.setName(sysUserDto.getUsername());
        sysUser.setDelFlag("0");

        sysUserService.save(sysUser);

        scheduleWebRegister.setId(UUID.randomUUID().toString());
        scheduleWebRegister.setName(sysUserDto.getUsername());
        scheduleWebRegister.setMobile(sysUserDto.getPhone());
        scheduleWebRegister.setRemark(sysUserDto.getOrg());
        scheduleWebRegister.setPersonId(sysUser.getId());
        scheduleWebRegister.setDelFlag("0");
        scheduleWebRegister.setCreateDate(new Date());

        scheduleWebRegisterService.save(scheduleWebRegister);

        return ResultGenerator.genSuccessResult();
    }



}
