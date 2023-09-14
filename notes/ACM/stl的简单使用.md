---
typora-copy-images-to: images
---







```c
(1) vector

(2) string

(3) queue,priority_queue

(4) stack

(5) deque

(6) set map multiset  multimap

(7) usordered_set   unordered_map   unordered_multiset  unordered_multimap

(8) bitset  位存储
```



### vector

```
size()
empty()
clear()  清空
front() / back()
push_back() / pop_back()
begin() / end() 迭代器
支持随即寻址，跟数组一样
```



#### vector的遍历

```c
#include <iostream>
#include <vector>

using namespace std;
int main()
{
	vector<int> a;
	for(int i = 0 ; i < 10 ; i++) a.push_back(i);
	
	// 三种遍历方式
	for(int i = 0 ; i < a.size() ; i++) cout << a[i] << ' ';
	cout << endl;
	
	for(vector<int>::iterator i = a.begin() ; i != a.end() ; i++) cout << *i << ' ';
	cout << endl;
	// 迭代器类型在C11之后也可以使用auto让编译器自己判断
	for(auto i = a.begin() ; i != a.end() ; i++) cout << *i << ' ';
	cout << endl;  
	
	
	for(auto x : a) cout << x << ' ';
	cout << endl; 
	return 0;
}
```



#### vector支持比较运算

```c
#include <iostream>
#include <vector>

using namespace std;
int main()
{
	vector<int> a(4 , 3) , b(3 , 4);
	// 4个3小于3个4 
	if(a < b) puts("a < b"); 
	return 0;
}
```

### 

### pair对组

```c
pair<int,string> p;
p.first; // pair的第一个元素
p.second; //pair的第二个元素
支持比较运算，以first为第一关键字，second为第二关键字
```



```c
// 构造pair
pair<int ,string> p;
p = make_pair(10 , "yxc");
// c11可以这么写
p = {20 , "abc"};
```



### string

```c
substr() // 截取一段string 
c_str() // 返回对应的字符数组的头指针
```

string其实是c++对char数组的封装。

```
size()
empty()
clear()
```

```c
#include <iostream>
#include <vector>

using namespace std;
int main()
{
	string s = "abc";
	s += "zqy";
	
	cout << s << endl;
	// substr() 第一个参数截取的开始下标，第二个参数截取的字符个数 
	cout << s.substr(1, 3) << endl;
	
	// 打印的话，取到 s 的字符数组的头指针进行打印 
	printf("%s\n" , s.c_str());
	return 0;
}
```





### queue

```c
size() , empty() , push(), pop() 弹出队头 , front() ,back()
```

queue没有clear()方法，如何清空queue

```c
queue<int> q;
q = queue<int>(); // 重新构造一个queue
```





### priority_queue(优先队列)

使用堆来实现，默认是大根堆。（大根堆，从大到小排序）

```c
push() , pop() 弹出堆顶元素, top()
```



如果想用小根堆怎么办？

黑科技一：

```c
priority_queue<int> heap;
heap.push(-20); // 存储数的时候我们将得到的数取反存入，这样就会从小到大排序
```



方式二：（定义小根堆）

```c
priority_queue<int , vector<int> , greater<int>> heap;
```





### stack

```c
push() , top() , pop()
```



### deque(双端队列)

但是deque效率太低

```
size()
empty()
clear()
front()
back()
push_back()/pop_back()
push_front()/pop_front()
begin() / end()
[]
```





### set map multiset  multimap

基于平衡二叉树（红黑树），动态维护有序序列

set不能有重复元素

multiset可以有重复元素



```
size()
empty()
clear()
set / multiset
	insert() 插入
	find() 查找一个数，如果不存在返回的是end的迭代器
	count() 返回某一个数的个数
	erase() 
		(1)参数如果是一个数x，删除所有x  O(k + logn) k:x的个数
		(2)输入一个迭代器，删除这个迭代器（也就是只删除一个）
	lower_bound() / upper_bound()  核心操作
		lower_bound(x)  返回大于等于x的最小的数的迭代器
		upper_bound(x)  返回大于x的最小的数的迭代器
```



```
map / multimap
	insert() 插入的是一个pair
	erase() 输入的参数是pair或者迭代器
	find()
	[] 像数组一样使用map
	lower_bound() / upper_bound()  
		lower_bound(x)  返回大于等于x的最小的数的迭代器
		upper_bound(x)  返回大于x的最小的数的迭代器
```

增删改查的时间复杂度 O（logn）

### unordered

unordered_set   unordered_map   unordered_multiset  unordered_multimap

基于哈希表实现



和上面类似，增删改查的时间复杂度是O（1）

不支持 lower_bound() / upper_bound()



### bitset ， 压位

```
开一个bool[1024] 的数组， 一个bool是一个字节B
1024B = 1KB
使用bitset的话，会使用1个bit来存储一个bool
1024 * 1bit = 128B
存储空间为之前的1/8
```



应用

```
假如需要存二维bool[10000][10000] ，大该需要10^8也就是100MB空间
如果题目要求空间限制为 64MB，
那么就可以使用bitset，大概需要 100MB / 8 = 12MBB
```



```
支持的操作 ： ~、 &、 |、 ^、 >>、 <<、==、!=、[]
~ 是按位取bai反运算符
count() 返回多少个1
any / none() 
	any() 判断是否至少有一个1
	none() 判断是否全为0
set()  把所有位变为1
set(k , v) 将第k为变为1
reset() 把所有为变为0
flip() 所有位取反  等价于~
flip(k) ……
```





