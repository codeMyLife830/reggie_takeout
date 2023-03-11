## 瑞吉外卖（基于SpringBoot和MyBatis-plus，来源黑马程序员）

### 后台管理系统

#### 1. 员工管理

#### 2. 分类管理

#### 3. 菜品管理
- 在原有基础上补充了起售、停售功能

#### 4. 套餐管理
- 在原有基础上补充了起售、停售功能

### 移动端应用

#### 1. 登录相关（验证码、登入、登出）
- 在原有基础上完善了前端的验证码功能，添加了单次验证码获取后的60秒等待时间

#### 剩余的正在完成，会实时更新...

### 安装与运行
#### 1. 命令行
1. 新建一个文件夹
2. 进入文件夹，打开GitBash，输入：
```
git clone https://github.com/codeMyLife830/reggie_takeout.git/
```
3. 运行idea，选择File > open选项，选中克隆到本地的项目文件夹
4. 刷新Maven，导入相关依赖
5. 修改项目文件夹中application.yml的数据库相关配置
6. 完成上述步骤后，即可运行
- 后台管理系统请访问：http://localhost:8080/backend/page/login/login.html
- 移动端应用请访问：http://localhost:8080/front/page/login.html
- 因页面使用HTML5实现响应式，需要使用浏览器的移动端模式才能正常查看
