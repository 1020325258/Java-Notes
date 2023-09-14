





### 数据统计（重定向版）

**freopen重定向方式读写文件**

```c++
#inclde<iostream>
#define LOCAL
#define INF = 100000000

int main()
{
	
#ifdef LOCAL
    // 读取文件和输出文件，文件存放在当前.cpp文件所在的文件夹 
	freopen("data.in" , "r" , stdin);
	freopen("data.out" , "w" , stdout);
#endif
	int x , n = 0 , max = -INF , min = INF , s = 0;
	while(scanf("%d" , &x) == 1)
	{
		s += x;
		if(x > max) max = x;
		if(x < min) min = x;
		n++;
	}
	//  输出最小值,最大值,平均数(保留三位小数) 
	printf("%d %d %.3f\n" , min , max , (double) s/n);
	
	return 0;
}
```



### 数据统计（fopen版）

如果比赛要求用文件输入输出，但禁止用重定向的方式 ， 又当如何呢？

可以使用fopen。

```c++
#include<iostream>
#define INF 100000000

using namespace std;

int main()
{
	FILE *fin , *fout;
	fin = fopen("data.in" , "rb");
	fout = fopen("data.out" , "wb");
	int x , n =  0 , min = INF , max = -INF , s = 0;
	while(fscanf(fin , "%d" , &x) == 1)
	{
		s += x;
		if(x < min) min = x;
		if(x > max) max = x;
		n++;
	}
	fprintf(fout , "%d %d %.3f\n" , min , max , (double) s/n);
	fclose(fin);
	fclose(fout);
	return 0;
}
```



先声明变量fin和fout（暂且不考虑FILE*） , 把 scanf 改成fscanf ， 第一个参数为fin ； 把printf 改成fprintf ， 第一个参数为 fout ， 最后执行 fclose ， 关闭两个文件。



### fopen和重定向的区别：

fopen的写法繁琐，但是灵活（可以反复打开并读写文件）





