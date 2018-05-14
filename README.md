# bedops4J
Java version of bedops

[offical site of bedops <https://bedops.readthedocs.io/en/latest/>](https://bedops.readthedocs.io/en/latest/)

[github of bedops <https://github.com/bedops/bedops>](https://github.com/bedops/bedops)


### 2018.4.16
突发发现貌似还有几个bug，最近没时间修，毕业之后再说吧

### 2018.3.21

我实现了bedops的大多数选项，有几个自身不常用，基本就放弃了。

I have implemented most of the options from bedops, and a few of them are not commonly used (at least for me) and basically give up.



在原本bedops的基础上做了基础改动。

Moreover, serveral changes based on orignal bedops have been made.



## 说明（Instructions）

1. 将bedops和closest-features的主要功能融合为一体，因此参数有所改动，具体如下：

   Combining the main functions of beedops and close-features into one, so the parameters have been changed, as follows

```bash
  usage: Bedops for Java
 -c,--complement          File1 [File]*
    --center              Only work with closest-features, print the
                          distance between center of two elements
 -cf,--closest-features   File1 File2
    --closest             Only work with closest-features, only print the
                          closest element
 -d,--difference          ReferenceFile File2 [File]*
    --dist                Only work with closest-features, print the
                          distance betweeen two elements
 -e,--element-of          [bp | percentage] ReferenceFile File2 [File]*
                          by default, -e 100% is used.  'bedops -e 1' is
                          also popular.
 -i,--intersect           File1 File2 [File]*
 -m,--merge               file paths that needs to be merged
 -n,--non-element-of      [bp | percentage] ReferenceFile File2 [File]*
                          by default, -n 100% is used.  'bedops -n 1' is
                          also popular.
 -o,--output <arg>        output file path
 -p,--partition           File1 [File]*
 -S,--pre-sort            Whether files need to be sorted in advance
 -s,--sort <arg>          file path that needs to be sorted
 -sd,--symmdiff           File1 File2 [File]*
    --stagger <arg>       [nt] only worked for chop
 -u,--everything          file paths that needs to concatenating
 -w,--chop <arg>          [bp] [--stagger <nt>] File1 [File]*
                          by default, -w 1 is used with no staggering*
```

   ​

2. 添加了一个external sort，就我目前的能力，速度比Linux 的sort命令略慢一些，慢在了一些我能力暂时做不到的地方，以后有机会再改。

   Added an external sort, which is slightly slower than Linux's sort command for my current capabilities. It slows down where some of my abilities are temporarily unavailable, and will change them later if  I have the opportunity.

   ​

3. 添加了-S选项，即强制在进行计算之前，将所有的文件进行一遍排序

   Added -S option to force all files to be sorted before computing

   ​

4. -e和-n的算法做了部分调整

   The -e and -n algorithms are partially adjusted

   > bedops的算法默认同一个bed文件中的位点不会有任何重合，因此，当有同一个文件中的位点有重合时，会导致漏掉部分结果
   >
   > The bedops algorithm consider there are no any overlaps between the sites in the same bed file. Therefore, when there are overlapped sites in the same file, it will missing some results.
   >
   > ​
   >
   > 我借助于Java8的RandomAccessFile，强制回溯文件读取的位置，进行重复的多次判断，保证不错过漏掉结果，当然运算速度自然会相应变慢
   >
   > Rely on Java8's RandomAccessFile to make repeated judgments, to make sure there are missed results. Of course, the computing speed will naturally slow down accordingly.

   ​

5. --closest-features、--closest连用时，会优选最后一个有重合的位点，算法原因，bedops就是如此，我没做改动

   When --closest-features and --closest are used in conjunction, the last overlapping site is preferred. The reason for the algorithm is that for beprots, I did not change it.

   ​

6. --closest-features时，两个位点间距离的计算，不是单纯的加减，还根据两个位点的相对位置，额外多+-了1，这个计算我按照bedops的方法算的

   When --closest-features, the calculation of the distance between two sites is not a simple addition and subtraction, but also based on the relative positions of the two sites, plus additional + - 1 accordingly, this calculation I calculate according to the method of bedops

   ​

7. 添加了--center，能够计算两个位点中心的距离

   Added --center to calculate the distance between center of two sites

## 碎碎念

最开始就是纯粹想重新练习一下Java的用法，然后没什么好练手的目标就从这个上手了

正好买了Java核心技术，就看书折腾，2018过年期间开的头，开学回来写了满打满算一个多星期，

前前后后加起来可能搞了一个多月，，终于搞完了

本来还想写一个swing的GUI，再说吧





