---
typora-copy-images-to: imgs
---



常用stl的自定义排序规则

```c++
vector自定义排序
vector<int> a;
// 排序的时候不仅可以使用a数组的内容排序，还可以使用其他数据对当前数组a排序
sort(a.begin(), a.end(), [&](int i, int j){retrun p[i] < p[j];});
```

```c
pair自定义排序
vector<pair<int,int>> a;
int main()
{
    a.push_back({2, 1});
    a.push_back({3, 0});
    sort(a.begin(), a.end(), [&](pair<int,int> p1, pair<int,int> p2){
        return p1.second < p2.second;
    });
    for(auto& x : a)
    {
        cout << x.first << ' ' << x.second << ' ';
    }
}
```



#### 1、匈牙利算法

#### 2、基环树

判断基环树，要满足两个条件：

- n == m，点数等于边数
- 连通

使用并查集来判断

##### **AcWing 4216. 图中的环** 

给定一个 nn 个点 mm 条边的**无向图**。

点的编号从 11 到 nn。

图中不含重边和自环。

请你对给定图进行判断，如果该图是一个**有且仅有**一个环的**连通图**，则输出 `YES`，否则输出 `NO`。

**输入格式**

第一行包含两个整数 n,mn,m。

接下来 mm 行，每行包含两个整数 a,ba,b，表示点 aa 和点 bb 之间存在一条**无向边**。

**输出格式**

如果该图是一个**有且仅有**一个环的**连通图**，则输出 `YES`，否则输出 `NO`。

**数据范围**

前三个测试点满足 1≤n≤101≤n≤10。
所有测试点满足 1≤n≤1001≤n≤100，0≤m≤n(n−1)20≤m≤n(n−1)2，1≤a,b≤n1≤a,b≤n。

输入样例1：

```
6 6
6 3
6 4
5 1
2 5
1 4
5 4

```

输出样例1：

```
YES

```



```c
#include <iostream>
#include <cstring>
#include <algorithm>

using namespace std;

const int N = 110;

int n, m;
int p[N];

int find(int x)
{
    if(x != p[x]) p[x] = find(p[x]);
    return p[x];
}

int main()
{
    cin >> n >> m;
    for(int i = 1; i <= n; i ++) p[i] = i;
    for(int i = 0; i < m; i ++)
    {
        int a, b;
        cin >> a >> b;
        int pa = find(a);
        int pb = find(b);
        if(pa != pb)
        {
            p[pa] = pb;
        }
    }
    int cnt = 0;
    for(int i = 1; i <= n; i ++)
        if(p[i] == i)
            cnt ++;
    if(cnt == 1 && n == m) puts("YES");
    else puts("NO");
    return 0;
}
```

#### 遍历队列queue

```c
#include<iostream>
#include<queue>

using namespace std;
int main(int argc, char* argv[]) {

   queue<int> myqueue;
   myqueue.push(1);
   myqueue.push(2);
   myqueue.push(3);

   int myqueue_size = myqueue.size();
   for(int i = 0; i < myqueue_size; i++) {   //myqueue_size 必须是固定值
      cout << myqueue.front() << endl;
      myqueue.push(myqueue.front());
      myqueue.pop();
   } 
}

```



### 二分图

## priority_queue

```c
riority_queue<int,vector<int>,greater<int>> q; 小根堆
```

#### Trie

##### Trie模板题

```c
#include <iostream>

using namespace std;

const int N = 100010;

int son[N][26], cnt[N], idx;
char str[N];

void insert(char *str)
{
    int p = 0;
    for (int i = 0; str[i]; i ++ )
    {
        int u = str[i] - 'a';
        if (!son[p][u]) son[p][u] = ++ idx;
        p = son[p][u];
    }
    cnt[p] ++ ;
}

int query(char *str)
{
    int p = 0;
    for (int i = 0; str[i]; i ++ )
    {
        int u = str[i] - 'a';
        if (!son[p][u]) return 0;
        p = son[p][u];
    }
    return cnt[p];
}

int main()
{
    int n;
    scanf("%d", &n);
    while (n -- )
    {
        char op[2];
        scanf("%s%s", op, str);
        if (*op == 'I') insert(str);
        else printf("%d\n", query(str));
    }

    return 0;
}

```

#### 字符串哈希

##### 模板

```c
#include <iostream>
#include <algorithm>

using namespace std;

typedef unsigned long long ULL; // 若溢出，会自动对2^64取模

const int N = 100010, P = 131;

int n, m;
char str[N];
ULL h[N], p[N];

ULL get(int l, int r)
{
    return h[r] - h[l - 1] * p[r - l + 1];
}

int main()
{
    scanf("%d%d", &n, &m);
    scanf("%s", str + 1);

    p[0] = 1;
    for (int i = 1; i <= n; i ++ )
    {
        h[i] = h[i - 1] * P + str[i];
        p[i] = p[i - 1] * P;
    }

    while (m -- )
    {
        int l1, r1, l2, r2;
        scanf("%d%d%d%d", &l1, &r1, &l2, &r2);

        if (get(l1, r1) == get(l2, r2)) puts("Yes");
        else puts("No");
    }

    return 0;
}
```



#### [2223. 构造字符串的总得分和](https://leetcode-cn.com/problems/sum-of-scores-of-built-strings/)

你需要从空字符串开始 **构造** 一个长度为 `n` 的字符串 `s` ，构造的过程为每次给当前字符串 **前面** 添加 **一个** 字符。构造过程中得到的所有字符串编号为 `1` 到 `n` ，其中长度为 `i` 的字符串编号为 `si` 。

- 比方说，`s = "abaca"` ，`s1 == "a"` ，`s2 == "ca"` ，`s3 == "aca"` 依次类推。

`si` 的 **得分** 为 `si` 和 `sn` 的 **最长公共前缀** 的长度（注意 `s == sn` ）。

给你最终的字符串 `s` ，请你返回每一个si 的 **得分之和** 。

 

**示例 1：**

```
输入：s = "babab"
输出：9
解释：
s1 == "b" ，最长公共前缀是 "b" ，得分为 1 。
s2 == "ab" ，没有公共前缀，得分为 0 。
s3 == "bab" ，最长公共前缀为 "bab" ，得分为 3 。
s4 == "abab" ，没有公共前缀，得分为 0 。
s5 == "babab" ，最长公共前缀为 "babab" ，得分为 5 。
得分和为 1 + 0 + 3 + 0 + 5 = 9 ，所以我们返回 9 。
```

**示例 2 ：**

```
输入：s = "azbazbzaz"
输出：14
解释：
s2 == "az" ，最长公共前缀为 "az" ，得分为 2 。
s6 == "azbzaz" ，最长公共前缀为 "azb" ，得分为 3 。
s9 == "azbazbzaz" ，最长公共前缀为 "azbazbzaz" ，得分为 9 。
其他 si 得分均为 0 。
得分和为 2 + 3 + 9 = 14 ，所以我们返回 14 。

```

 

**提示：**

- `1 <= s.length <= 105`
- `s` 只包含小写英文字母。



```c
class Solution {
public:
const int P = 31;
unsigned long long h[100010], p[100010];

    long long query(int l, int r) {
        return h[r] - h[l-1] * p[r-l+1];
    }
    long long sumScores(string s) {
        int n = s.size();
        p[0] = 1;
        h[0] = 0;
        for(int i = 0; i < n; i ++) {
            p[i+1] = p[i] * P;
            h[i+1] = h[i] * P + s[i];
        }
        // exit(0);
        long long res = 0;
        for(int len = 1, i = n; i >= 1; i --, len ++)
        {
            // string t = s.substr(i-1, len);
            // cout << "I:" << i << ' ' << t << endl;
            if(s[i-1] != s[0]) continue;
            int l = 0, r = len;
            while(l < r)
            {
                int mid = (l + r + 1) / 2;
                long long q1 = query(i, i+mid-1);
                long long q2 = query(1, mid);
                if(q1 == q2)
                {
                    l = mid;
                }else
                {
                    r = mid - 1;
                }
            }
            res += l;
        }
        // res += n;
        return res;
    }
};
```





```
0 0->1 0
vis:
1 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
1 0->2 0
vis:
1 0 0 0 0 
1 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
2 0->3 0
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
0 0 0 0 0 
0 0 0 0 0 
3 0->3 1
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
0 0 0 0 0 
3 0->4 0
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
1 1 0 0 0 
0 0 0 0 0 
3 1->4 1
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
1 1 0 0 0 
1 0 0 0 0 
4 1->4 2
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
1 1 0 0 0 
1 1 0 0 0 
4 2->4 3
vis:
1 0 0 0 0 
1 0 0 0 0 
1 0 0 0 0 
1 1 0 0 0 
1 1 1 0 0 

```



#### 对于string转longlong若有可能爆longlong需要进行特判

```c
long long res = 0;
for(int i = 0; i < str.size(); i ++)
{
	if(res >= LONG_LONG_MAX / 10)  // 特判，防止溢出
    {
		res = LONG_LONG_MAX;
      	 break;
    }
  	res = res * 10 + str[i] - '0';
}
```



#### 01背包

```c
#include <iostream>

using namespace std;

const int N = 1010;
int n , m;
int w[N] , v[N];
int dp[N][N];

int main()
{
    scanf("%d%d" , &n , &m);
    for(int i = 1 ; i <= n ; i++)
    {
        scanf("%d%d" , &v[i] , &w[i]);
    }

    for(int i = 1 ; i <= n ; i++)
    {
        for(int j = 0 ; j <= m ; j++)
        {
            dp[i][j] = dp[i-1][j];
            if(j >= v[i])
            {
                dp[i][j] = max(dp[i][j] , dp[i-1][j-v[i]] + w[i]);
            }
        }
    }
  	
    cout << dp[n][m] << endl;
    return 0;
}
```

```c
一维优化
for(int i = 1 ; i <= n ; i++)
{
    for(int j = m ; j >= v[i] ; j --)
    {
        f[j] = Math.max(f[j] , f[j-v[i]] + w[i]);
    }
}        
```

#### 完全背包

##### 朴素算法
```c
import java.util.*;
public class Main{
    public static final int N = 1010;
    public static int[] w = new int[N];
    public static int[] v = new int[N];
    public static int[][] f = new int[N][N];
    public static int n , m;
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        for(int i = 1 ; i <= n ; i++)
        {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
        }
        
        for(int i = 1 ; i <= n ; i++)
        {
            for(int j = 0 ; j <= m ; j ++)
            {
                for(int k = 0 ; k * v[i] <= j ; k ++)
                {
                    /*
                      在 f[i][j] 和 选取k个第i个物品 中选取最大的值 
                      这样的话,三层循环就造成了时间复杂度很高,速度很慢.
                    */
                    f[i][j] = Math.max(f[i][j] , f[i-1][j - v[i] * k ] + w[i] * k);
                
                  
            }
        }
        System.out.println(f[n][m]);
    }
}
```
##### 优化后的朴素算法
```c
import java.util.*;
public class Main{
    public static final int N = 1010;
    public static int[] w = new int[N];
    public static int[] v = new int[N];
    public static int[][] f = new int[N][N];
    public static int n , m;
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        for(int i = 1;  i <= n ; i++)
        {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
        }
        
        for(int i = 1 ; i <= n ; i++)
        {
            for(int j = 0 ; j <= m ; j++)
            {
                f[i][j] = f[i-1][j];
                if(j >= v[i])
                {
                    f[i][j] = Math.max(f[i][j] , f[i][j-v[i]] + w[i]);
                }
            }
        }
        System.out.println(f[n][m]);
    }
}
```
##### 一维算法
```c
import java.util.*;
public class Main{
    public static final int N = 1010;
    public static int[] w=  new int[N];
    public static int[] v = new int[N];
    public static int[] f = new int[N];
    public static int n , m;
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        for(int i =1 ; i <= n ; i++)
        {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
        }
        
        for(int i = 1 ; i <= n ; i++)
        {
            for(int j = v[i] ; j <= m ; j++)
            {
                /*
                  和 01 背包问题不同的是,这里可以从小到大开始遍历,因为每次f[i][j]=Math.max(f[i][j],f[i][j-v[i]]+w[i])
                  所以当遍历到f[i][j] 的时候 f[j-v[i]][j]已经被更新过了,已经是f[i][j-v[i]]的了,所以可以从小开始遍历
                  但是01背包的话f[i][j]= Math.max(f[i][j],f[i-1][j-v[i]]+w[i])是从f[i-1]遍历过来的,需要从大到小开始遍历
                */
                f[j] = Math.max(f[j] , f[j-v[i]] + w[i]);
            }
        }
        System.out.println(f[m]);
    }
}
```



#### 扫描线

对于不规则的多个矩形，可以使用扫描线

https://leetcode-cn.com/problems/the-skyline-problem/

**LeetCode 218 天际线问题**

![1651916685373](C:\Users\10203\Desktop\java_back_end_learning_notes\数据结构与算法笔记\imgs/1651916685373.png)

![1651916695242](C:\Users\10203\Desktop\java_back_end_learning_notes\数据结构与算法笔记\imgs/1651916695242.png)

```c
class Solution {
public:
    vector<vector<int>> getSkyline(vector<vector<int>>& buildings) {
        vector<vector<int>> res;
        vector<pair<int,int>> points;
        multiset<int> heights;
        // 将所有点存储下来
        for(int i = 0; i < buildings.size(); i ++)
        {
            points.push_back({buildings[i][0], -buildings[i][2]}); // 左端点存-h
            points.push_back({buildings[i][1], buildings[i][2]}); // 右端点存h
        }
        sort(points.begin(), points.end());
        heights.insert(0);
        for(int i = 0; i < points.size(); i ++)
        {
            // 左端点
            if(points[i].second < 0) 
            {
                // 如果当前高度是最高的话，就加入答案中去
                if(-points[i].second > *heights.rbegin())
                {
                    res.push_back({points[i].first, -points[i].second});
                }
                heights.insert(-points[i].second);
            }else // 右端点
            {
                // 删除当前线段的高度
                heights.erase(heights.find(points[i].second));
                if(points[i].second > *heights.rbegin())
                {
                    res.push_back({points[i].first, *heights.rbegin()});
                }
            }
        }
        return res;
    }
};
```



#### 树状数组

模板

##### 1、楼兰图腾

```
在完成了分配任务之后，西部 314314 来到了楼兰古城的西部。

相传很久以前这片土地上(比楼兰古城还早)生活着两个部落，一个部落崇拜尖刀(V)，一个部落崇拜铁锹(∧)，他们分别用 V 和 ∧ 的形状来代表各自部落的图腾。

西部 314314 在楼兰古城的下面发现了一幅巨大的壁画，壁画上被标记出了 nn 个点，经测量发现这 nn 个点的水平位置和竖直位置是两两不同的。

西部 314314 认为这幅壁画所包含的信息与这 nn 个点的相对位置有关，因此不妨设坐标分别为 (1,y1),(2,y2),…,(n,yn)(1,y1),(2,y2),…,(n,yn)，其中 y1∼yny1∼yn 是 11 到 nn 的一个排列。

西部 314314 打算研究这幅壁画中包含着多少个图腾。

如果三个点 (i,yi),(j,yj),(k,yk)(i,yi),(j,yj),(k,yk) 满足 1≤i<j<k≤n1≤i<j<k≤n 且 yi>yj,yj<ykyi>yj,yj<yk，则称这三个点构成 V 图腾;

如果三个点 (i,yi),(j,yj),(k,yk)(i,yi),(j,yj),(k,yk) 满足 1≤i<j<k≤n1≤i<j<k≤n 且 yi<yj,yj>ykyi<yj,yj>yk，则称这三个点构成 ∧ 图腾;

西部 314314 想知道，这 nn 个点中两个部落图腾的数目。

因此，你需要编写一个程序来求出 V 的个数和 ∧ 的个数。

```

- 输入格式

第一行一个数 nn。

第二行是 nn 个数，分别代表 y1，y2,…,yny1，y2,…,yn。

- 输出格式

两个数，中间用空格隔开，依次为 `V` 的个数和 `∧` 的个数。

- 数据范围

对于所有数据，n≤200000n≤200000，且输出答案不会超过 int64int64。
y1∼yny1∼yn 是 11 到 nn 的一个排列。

- 输入样例：

```
5
1 5 3 2 4

```

- 输出样例：

```
3 4
```

```c
#include <iostream>
#include <cstring>
#include <algorithm>

using namespace std;

const int N = 200010;

int n;
int a[N];
int tr[N];
int upper[N], lower[N];

int lowbit(int x){
    return x & -x;
}

int add(int x, int c)
{
    for(int i = x; i <= n; i += lowbit(i)) tr[i] += c;
}

int sum(int x)
{
    int res = 0;
    for(int i = x; i; i -= lowbit(i))
    {
        res += tr[i];
    }
    return res;
}

int main()
{
    cin >> n;
    for(int i = 1; i <= n; i ++) cin >> a[i];
    
    // 先从左向右计算
    for(int i = 1; i <= n; i ++)
    {
        int y = a[i];
        upper[i] = sum(n) - sum(y); // upper[i]表示1~i-1中高度高于a[i]的个数
        lower[i] = sum(y-1);        // lower[i]同理
        add(y, 1);                  // 将高度为y的个数加1
    }
    // 再从右向左计算
    memset(tr, 0, sizeof tr);
    long long res1 = 0, res2 = 0;
    for(int i = n; i; i --)
    {
        int y = a[i];
        res1 += upper[i] * (long long) (sum(n) - sum(y));
        res2 += lower[i] * (long long) (sum(y-1));
        add(y, 1);
    }
    cout << res1 << ' ' << res2 << endl;
    return 0;
}
```

##### 2、一个简单的整数问题

```
给定长度为 N 的数列 A，然后输入 M 行操作指令。

第一类指令形如 C l r d，表示把数列中第 l∼r 个数都加 d。

第二类指令形如 Q x，表示询问数列中第 x 个数的值。

对于每个询问，输出一个整数表示答案。

输入格式
第一行包含两个整数 N 和 M。

第二行包含 N 个整数 A[i]。

接下来 M 行表示 M 条指令，每条指令的格式如题目描述所示。

输出格式
对于每个询问，输出一个整数表示答案。

每个答案占一行。

数据范围
1≤N,M≤105,
|d|≤10000,
|A[i]|≤109
输入样例：
10 5
1 2 3 4 5 6 7 8 9 10
Q 4
Q 1
Q 2
C 1 6 3
Q 2
输出样例：
4
1
2
5
```

 

```c
#include <iostream>
#include <cstring>
#include <algorithm>

using namespace std;

typedef long long LL;

const int N = 100010;

int n, m;
int a[N], tr[N];

LL lowbit(int x) {
    return x & -x;
}

void add(int x, int c)
{
    for(int i = x; i <= n; i += lowbit(i)) tr[i] += c;
}

LL sum(int x)
{
    LL res = 0;
    for(int i = x; i; i -= lowbit(i)) res += tr[i];
    return res;
}

int main()
{
    cin >> n >> m;
    for(int i = 1; i <= n; i ++) cin >> a[i];
    
    for(int i = 1; i <= n; i ++) add(i, a[i] - a[i-1]); // 使用差分来做
    
    while (m -- ){
        char op[2];
        int l, r, c;
        scanf("%s%d", op, &l);
        if(*op == 'C')
        {
            scanf("%d%d", &r, &c);
            add(l, c), add(r+1, -c);
        }else
        {
            printf("%lld\n", sum(l));
        }
    }
    return 0;
}
```

##### 3、一个简单的整数问题2

```
给定一个长度为 N 的数列 A，以及 M 条指令，每条指令可能是以下两种之一：

C l r d，表示把 A[l],A[l+1],…,A[r] 都加上 d。
Q l r，表示询问数列中第 l∼r 个数的和。
对于每个询问，输出一个整数表示答案。

输入格式
第一行两个整数 N,M。

第二行 N 个整数 A[i]。

接下来 M 行表示 M 条指令，每条指令的格式如题目描述所示。

输出格式
对于每个询问，输出一个整数表示答案。

每个答案占一行。

数据范围
1≤N,M≤105,
|d|≤10000,
|A[i]|≤109
输入样例：
10 5
1 2 3 4 5 6 7 8 9 10
Q 4 4
Q 1 10
Q 2 4
C 3 6 3
Q 2 4
输出样例：
4
55
9
15
```

解析

```

还是需要差分来做，先根据原数组做出差分数组b，那么a[i]为：
a[1] = b[1]
a[2] = b[1] + b[2]
a[3] = b[1] + b[2] + b[3]
...
a[x] = b[1] + b[2] + b[3] + ... + b[x]
想要求区间和，需要求前缀和，那么求1~i的前缀和的话，就是求下面这个三角形的和：
a[1]  b1
a[2]  b1  b2
a[3]  b1  b2  b3
...
a[x]  b1  b2  b3  ..  bx
对于上边三角形的和不好求，我们可以把三角形补全
      b1  b2  b3  ..  bx
a[1]  b1  b2  b3  ..  bx
a[2]  b1  b2  b3  ..  bx
a[3]  b1  b2  b3  ..  bx
...
a[x]  b1  b2  b3  ..  bx
这样的话，要求下边的三角形的话，只需要让正方形的和-上边三角形的和：
即res = (x+1)(b1+..+bx) - (1*b1 + 2*b2 + 3*b3 + .. + x*bx)
那么维护两个前缀和：tr1表示b[i]的前缀和,tr2表示i*bi的前缀和，即可求出a的前缀和
```

```c
#include <iostream>
#include <cstring>
#include <algorithm>

using namespace std;

const int N = 100010;

typedef long long LL;

int n, m;

LL a[N];
LL tr1[N]; // 记录d[i]前缀和
LL tr2[N]; // 记录d[i] * i的前缀和

LL lowbit(int x) {
    return x & -x;
}

void add(LL tr[], int x, LL c)
{
    for(int i = x; i <= n; i += lowbit(i)) 
    {
        tr[i] += c;
    }
}

LL sum(LL tr[], int x) 
{
    LL s = 0;
    for(int i = x; i; i -= lowbit(i))
    {
        s += tr[i];
    }
    return s;
}

LL pre_sum(int x) 
{
    return sum(tr1, x) * (x + 1) - sum(tr2, x);
}

int main()
{
    cin >> n >> m;
    for(int i = 1; i <= n; i ++) cin >> a[i];
    for(int i = 1; i <= n; i ++)
    {
        int b = a[i] - a[i-1];
        add(tr1, i, b);
        add(tr2, i, (LL) b * i);
    }
    while (m -- )
    {
        char op;
        int l, r, d;
        cin >> op >> l >> r;
        if(op == 'Q') 
        {
            cout << pre_sum(r) - pre_sum(l-1) << endl;
        }else 
        {
            cin >> d;
            add(tr1, l, d), add(tr1, r+1, -d);
            add(tr2, l, l*d), add(tr2, r+1, -d*(r+1) );
        }
    }
    return 0;
}
```



1:39