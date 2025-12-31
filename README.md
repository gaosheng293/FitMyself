# FitMyself 🏋️‍♂️

**FitMyself** 是一款高度可定制的个人健身记录 Android 应用。它不仅仅是一个记录本，更是一个完整的训练循环管理系统，帮助你规划分化训练、追踪组间休息、记录训练容量，并回顾历史数据。

> **Project by:** sheng  
> **Repository:** [https://github.com/gaosheng293/FitMyself](https://github.com/gaosheng293/FitMyself)

---

## 📱 功能特性 (Features)

### 1. 高度自由的计划管理
* **自定义分化 (Custom Splits)**: 除了内置的经典分化（如五分化），支持用户创建专属的训练分化模式。
* **循环编排 (Cycle Arrangement)**: 通过拖拽（Drag & Drop）自由安排每周的训练部位顺序。
* **动态动作库**: 内置丰富的动作库，同时支持**手动添加**自定义动作，且支持**长按删除**管理。

### 2. 沉浸式训练体验
* **今日看板**: 首页自动显示当前循环日的训练部位和计划动作，进度一目了然。
* **实时记录**: 便捷记录每一组的重量（Weight）和次数（Reps），包含非空校验防止错误数据。
* **智能计时器**: 完成一组后自动触发正向计时（Stopwatch），帮助你精确控制组间歇。

### 3. 数据持久化与历史
* **本地数据库**: 使用 **Android Room** 数据库，确保数据安全存储在本地。
* **历史回顾**: 采用折叠式卡片设计（Folding List），按日期归档训练记录，点击即可展开查看详细数据（重量、次数、休息时长）。
* **模板同步**: 修改循环或动作后，支持同步更新数据库模板。

---

## 🛠️ 技术栈 (Tech Stack)

* **Language**: Java
* **Architecture**: MVVM (implied via Room/DAO structure)
* **Database**: Android Jetpack Room
* **UI Components**:
    * Material Design (FloatingActionButton, CardView, CoordinatorLayout)
    * RecyclerView (Multi-type adapters, ItemTouchHelper for drag & drop)
    * ConstraintLayout & LinearLayout
* **Compatibility**: Android 10+ (API Level 29+) recommended

---

## 📸 截图 (Screenshots)

| 首页看板 (Dashboard) | 训练循环 (Cycle) | 训练记录 (Training) | 历史记录 (History) |
|:---:|:---:|:---:|:---:|
| *(在此处放置截图)* | *(在此处放置截图)* | *(在此处放置截图)* | *(在此处放置截图)* |

---

## 🚀 快速开始 (Getting Started)

1.  **克隆项目**
    ```bash
    git clone [https://github.com/gaosheng293/FitMyself.git](https://github.com/gaosheng293/FitMyself.git)
    ```

2.  **打开项目**
    * 启动 Android Studio。
    * 选择 `File` -> `Open`，找到克隆下来的 `FitMyself` 文件夹。

3.  **构建与运行**
    * 等待 Gradle Sync 完成。
    * 连接 Android 设备或启动模拟器。
    * 点击 `Run` (绿色三角形按钮)。

---

## 🤝 贡献 (Contributing)

欢迎提交 Issue 或 Pull Request 来改进这个项目！

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

---

## 📄 License

此项目采用 MIT 开源协议。

Copyright © 2025 **sheng**.
