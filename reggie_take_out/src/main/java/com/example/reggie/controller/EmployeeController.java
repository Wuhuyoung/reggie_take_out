package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密处理, 得到加密后的字符串
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库中员工数据信息
        Employee emp = employeeService.selectByUsername(employee.getUsername());

        //3、如果没有查询到, 则返回登录失败结果
        if(emp == null) {
            return Result.error("用户名不存在");
        }
        //4、密码比对，如果不一致, 则返回登录失败结果
        if(!password.equals(emp.getPassword())) {
            return Result.error("密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0) {
            return Result.error("账号已禁用");
        }
        //6、登录成功，将员工id存入Session, 并返回登录成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //1、赋予初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //2、创建人和时间的信息
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前操作用户的id
        //Long id = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(id);
        //employee.setUpdateUser(id);

        //3、调用service方法保存用户
        if(employeeService.save(employee)) {
            return Result.success("新增员工成功");
        }
        return Result.error("新增员工失败");
    }

    /**
     * 分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Employee>> selectPage(int page, int pageSize, String name) {
        Page<Employee> employeePage = employeeService.selectPageByCondition(page, pageSize, name);
        if(employeePage != null) {
            return Result.success(employeePage);
        }
        return Result.error("查询失败");
    }

    /**
     * 修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("employee:" + employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        if(employeeService.update(employee)) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        Employee employee = employeeService.selectById(id);
        if(employee != null) {
            return Result.success(employee);
        }
        return Result.error("没有查询到对应员工信息");
    }
}
