# Imitation Tmall Mall
Mini Tmall Mall is a comprehensive B2C e-commerce platform based on the SSM framework. The requirements design mainly refers to the shopping process of Tmall Mall: users start from registration, complete login, browse products, add to the shopping cart, place orders, confirm receipt, and evaluate a series of operations. As one of the core components of the simulated Tmall mall system, the Tmall data management backend using the SSM framework includes modules such as product management, order management, category management, user management, and transaction volume statistics, achieving one-stop management and maintenance of the entire mall. (For reference only, if you have any questions, please forgive me! If you think it's good, please help recommend it. Thank you all handsome guys and beautiful women!)

The backend page is compatible with modern browsers such as IE10 and above, and Chrome, Edge, and Firebox perform best.

# Deployment method(Master - Basic version, feature1.0- Newer version)
1. The project is developed using IntelliJ IDEA, please use the version control checkout function of IntelliJ IDEA, enter“ https://gitee.com/HaiTao87/TmallDemo.git ”Just pull the item. 
2. The project database master is divided into MySQL version 5.7, and the feature is divided into MySQL version 8.0 and above. Please download the SQL file from the attachment of Code Cloud or the SQL folder under Resources and import it into the database. 
3. After opening the project using IDEA, refresh the project in the Maven panel and download the dependency package. (Project jdk is 1.8) 
4. Start the springboot project in IDEA (either in run or debug mode).
5. Please refer to the attached SQL file for the account name and password, or the SQL file in the SQL folder under Resources (explained below the account password on the front-end and back-end pages).
   ![输入图片说明](https://images.gitee.com/uploads/images/2020/1016/150457_5c0c7304_996301.png "屏幕截图.png")
6. Swagger interface document address: http://localhost:8082/tmall/swagger-ui.html
7. Druid Monitor Monitoring address：http://localhost:8082/tmall/druid/sql.html (Account: admin Password: 123456)

Attention: It is normal for the order chart in the backend management interface to have no data. The chart displays the transaction volume for the past 7 days.

---Background interface (partial) - Access address: http://localhost:8082/tmall/admin/login (The account name and password are in the admin table)
![登录界面](https://gitee.com/uploads/images/2018/0526/222324_71d64249_1616166.png "2018-05-26_221417.png")
![首页](https://gitee.com/uploads/images/2018/0526/222349_00d5df29_1616166.png "2018-05-26_221445.png")
![产品列表](https://gitee.com/uploads/images/2018/0526/222414_c3a74f51_1616166.png "2018-05-26_221454.png")
![添加产品](https://gitee.com/uploads/images/2018/0526/222440_813cf8d7_1616166.png "2018-05-26_221504.png")
![产品详情](https://gitee.com/uploads/images/2018/0526/222457_7727da44_1616166.png "2018-05-26_221513.png")
![产品类别列表](https://gitee.com/uploads/images/2018/0526/222515_0f605a1a_1616166.png "2018-05-26_221522.png")
![用户列表](https://gitee.com/uploads/images/2018/0526/222531_2ddbba60_1616166.png "2018-05-26_221530.png")
![用户详情](https://gitee.com/uploads/images/2018/0526/222542_f816afc9_1616166.png "2018-05-26_221539.png")
![订单列表](https://gitee.com/uploads/images/2018/0526/222601_ac370928_1616166.png "2018-05-26_221547.png")
![订单详情](https://gitee.com/uploads/images/2018/0526/222628_e539faf6_1616166.png "2018-05-26_221554.png")
![管理员详情](https://gitee.com/uploads/images/2018/0526/222839_911d4e0d_1616166.png "2018-05-26_221607.png")
---Front end interface (partial) - Access address http://localhost:8082/tmall/login (Account name and password in the user table)
![登陆界面](https://gitee.com/uploads/images/2018/0526/223030_17b28619_1616166.png "2018-05-26_221715.png")
![首页](https://gitee.com/uploads/images/2018/0526/223018_14e999f1_1616166.png "2018-05-26_221703.png")
![产品详情](https://gitee.com/uploads/images/2018/0526/223044_e481ec5f_1616166.png "2018-05-26_221725.png")
![下单界面](https://gitee.com/uploads/images/2018/0526/223100_ef6e9612_1616166.png "2018-05-26_221837.png")
![订单列表](https://gitee.com/uploads/images/2018/0526/223117_dfd64b43_1616166.png "2018-05-26_221901.png")
![确认收货](https://gitee.com/uploads/images/2018/0526/223220_71e2ee3d_1616166.png "2018-05-26_221911.png")
![产品列表](https://gitee.com/uploads/images/2018/0526/223233_18e131a5_1616166.png "2018-05-26_222006.png")
![购物车](https://gitee.com/uploads/images/2018/0526/223245_3f80d8f4_1616166.png "2018-05-26_223157.png")