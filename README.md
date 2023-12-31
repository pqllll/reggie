# 项目简介

本项目是一个外卖点餐系统，采用前后端分离的架构，服务端采用Spring Boot和MyBatis框架，数据库使用MySQL，客户端为微信小程序。主要功能包括员工管理、菜品管理、套餐管理、下单、收货地址管理、订单查询等。

# 服务端

## 技术栈

- Spring Boot
- MyBatis
- MySQL
- Redis

## 功能模块

### 员工管理

员工管理模块包括员工信息的增删改查。实现了用户权限控制，只有管理员才能对员工信息进行修改和删除操作。

### 菜品管理

菜品管理模块包括菜品信息的增删改查。实现了菜品分类功能，可以按照菜品分类查询菜品信息。

### 套餐管理

套餐管理模块包括套餐的增删改查。实现了套餐与菜品的关联，一个套餐可以包含多个菜品。

### 下单

下单模块包括用户下单、订单支付和商家接单等功能。实现了下单时自动计算订单金额和优惠金额，以及商家接单后推送消息给用户。

### 收货地址管理

收货地址管理模块包括用户收货地址的增删改查。实现了用户收货地址的多个收件人管理，支持设置默认收件人。

### 订单查询

订单查询模块包括用户订单的查询和商家订单的查询。实现了订单状态的更新和订单详情的展示。

## 技术难点

### 多表关联查询

在套餐管理模块中，需要查询套餐关联的菜品信息。使用MyBatis的一对多映射，将套餐表和菜品表进行关联查询。

### 分布式锁

在下单模块中，为了防止高并发情况下出现库存不足或者重复下单等问题，采用Redis实现分布式锁。

### 数据库读写分离

为了提升服务器的性能，采用MySQL的主从复制实现数据库读写分离。写操作由主库处理，读操作由从库处理，减轻主库的负担。

# 客户端

## 技术栈

- 微信小程序
- JavaScript

## 功能模块

### 手机号短信登录

客户端通过手机号和短信验证码进行登录，实现了短信验证码的发送和验证功能。

### 套餐展示

套餐展示模块展示了商家提供的所有套餐，用户可以浏览套餐详情和菜品信息。

### 下单

下单模块包括用户选择套餐、填写收货地址、支付订单等功能。实现了下单时自动计算订单金额和优惠金额。

### 收货地址管理

收货地址管理模块可以让用户增删改查收货地址信息，支持设置默认收件人。

### 订单查询

订单查询模块包括用户订单的查询和商家订单的查询。实现了订单状态的更新和订单详情的展示。


# 项目优化

为了提升系统的性能和可扩展性，对项目进行了如下优化：

- 使用Redis缓存热门数据；
- 使用分布式锁避免高并发问题；
- 使用读写分离优化数据库性能；
- 使用原子性操作保证数据一致性；
- 使用Nginx负载均衡实现多服务器部署。

# 总结

本项目锻炼了我Spring Boot和MyBatis框架的应用能力，提高了我对于多表关联查询、分布式锁、数据库读写分离、小程序支付等技术的掌握程度，同时也让我了解到如何进行项目的优化和部署。
