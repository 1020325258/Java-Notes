



Trie树

（1）快速存储和查找字符串集合的数据结构

如果使用trie做的话，存储的英文一般是26个或52个，数据量不会很大。



### 835. Trie字符串统计                    

​                

维护一个字符串集合，支持两种操作：

1. “I x”向集合中插入一个字符串x；
2. “Q x”询问一个字符串在集合中出现了多少次。

共有N个操作，输入的字符串总长度不超过 105105，字符串仅包含小写英文字母。

**输入格式**

第一行包含整数N，表示操作数。

接下来N行，每行包含一个操作指令，指令为”I x”或”Q x”中的一种。

**输出格式**

对于每个询问指令”Q x”，都要输出一个整数作为结果，表示x在集合中出现的次数。

每个结果占一行。

**数据范围**

1≤N≤2∗1041≤N≤2∗104

**输入样例：**

```
5
I abc
Q abc
Q ab
I ab
Q ab

```

**输出样例：**

```
1
0
1
```







```java
import java.util.*;
public class Main{
    
    public static final int N = 100010;
    public static int[][] son = new int[N][26];
    public static int[] cnt = new int[N];
    public static int idx = 0;
    public static void insert(char[] str){
        int p = 0 ;
        for(int i = 0 ; i < str.length ; i ++)
        {
            int u = str[i] - 'a';
            if(son[p][u] == 0) son[p][u] = ++idx;
            p = son[p][u];
        }
        cnt[p]++;        
    }
    
    public static int query(char[] str){
        int p = 0;
        for(int i = 0 ; i < str.length ; i++)
        {
            int u = str[i] - 'a';
            if(son[p][u] == 0) return 0;
            p = son[p][u];
        }
        return cnt[p];
    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        sc.nextLine();
        while(m-- > 0)
        {
            String[] str = sc.nextLine().split(" ");
            if(str[0].equals("I"))
            {
                char[] chs = str[1].toCharArray();
                insert(chs);
            }else
            {
                char[] chs = str[1].toCharArray();
                System.out.println(query(chs));
            }
        }
    }
}
```







### 143. 最大异或对                        

在给定的N个整数A1，A2……ANA1，A2……AN中选出两个进行xor（异或）运算，得到的结果最大是多少？

**输入格式**

第一行输入一个整数N。

第二行输入N个整数A1A1～ANAN。

**输出格式**

输出一个整数表示答案。

**数据范围**

1≤N≤1051≤N≤105,
0≤Ai<2310≤Ai<231

**输入样例：**

```
3
1 2 3

```

**输出样例：**

```
3
```







```java
import java.util.*;
public class Main{
    public static final int N = 100010; //N的数据范围是10w
    public static final int M = 3000000; //定义数组30w,一个int数的二进制有31位,最多有10w个数,所以最多310w
    public static int idx = 0;
    public static int[][] son = new int[M][2];
    public static int[] a = new int[N];
    
    public static void insert(int x){
        int p = 0;
        // 一个int型数的二进制有31位,从高位开始遍历
        for(int i = 30 ; i >= 0 ; i--)
        {
            int u = x >> i & 1 ; //看x的第i位是否是1
            if(son[p][u] == 0) son[p][u] = ++idx;
            p = son[p][u];
        }
    }
    
    public static int query(int x){
        // 使用res记录x与trie树中的最大值
        int p = 0 , res = 0;
        for(int i = 30 ; i >= 0 ; i--)
        {
            int u = x >> i & 1;
            if(son[p][u^1] != 0)
            {
                res += 1 << i; // 这一步其实包含了异或操作,假如我这一位是1,对应的0可以走的话,那就加上这一位
                p = son[p][u^1]; // 走向下一步
            }else
            {
                // 如果我是1,对应的0不可以走的话,就只能走我这个1, 1^1还是0,所以不用改变res
                p = son[p][u];
            }
        }
        return res;
    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        for(int i = 0 ; i < n ; i ++)
        {
            a[i] = sc.nextInt();
            insert(a[i]);
        }
        
        int res = 0;
        for(int i = 0 ; i < n ; i++)
        {
            res = Math.max(res , query(a[i]));
        }
        System.out.println(res);
    }
}
```

