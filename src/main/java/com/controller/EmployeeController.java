package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Employee;
import com.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将密码转为md5加密方式
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.通过username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.比对数据库的username
        if (emp == null) {
            return R.error("登陆失败");
        }
        //4.比对password
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        //5.是否禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //6.成功,把员工id放入Session(浏览器)
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
      /*
       自动注入
       employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now())
        //获取当前登录用户
        long empId = (long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setCreateUser(empId);
      */
        employeeService.save(employee);

        return R.success("新增员工成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Employee> pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        if (name != null) {
            queryWrapper.like(Employee::getName, name);
        }
        //按序排列
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> Update(HttpServletRequest request, @RequestBody Employee employee) {
        //  Long empId = (Long) request.getSession().getAttribute("employee");
        //  employee.setUpdateTime(LocalDateTime.now());
        //  employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}

