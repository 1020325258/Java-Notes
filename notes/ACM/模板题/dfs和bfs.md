



### 847. 图中点的层次                       

给定一个n个点m条边的有向图，图中可能存在重边和自环。

所有边的长度都是1，点的编号为1~n。

请你求出1号点到n号点的最短距离，如果从1号点无法走到n号点，输出-1。

**输入格式**

第一行包含两个整数n和m。

接下来m行，每行包含两个整数a和b，表示存在一条从a走到b的长度为1的边。

**输出格式**

输出一个整数，表示1号点到n号点的最短距离。

**数据范围**

1≤n,m≤1051≤n,m≤105

**输入样例：**

```
4 5
1 2
2 3
3 4
1 3
1 4

```

**输出样例：**

```
1
```







```java
import java.util.*;
public class Main{
    public static final int N = 100010;
    public static int[] h = new int[N];
    public static int[] e = new int[N];
    public static int[] ne = new int[N];
    public static int[] d = new int[N];
    public static int[] q = new int[N];
    public static int idx = 0 , n , m , hh = 0 , tt = -1;
    
    public static void fill(int[] a , int x){
        for(int i = 0 ; i < a.length ; i++)
        {
            a[i] = x;
        }
    }
    
    public static void add(int a , int b){
        e[idx] = b;
        ne[idx] = h[a];
        h[a] = idx++;
    }
    
    public static int bfs(){
        d[1] = 0;
        q[++tt] = 1;
        while(tt >= hh)
        {
            int x = q[hh++];
            for(int i = h[x] ; i != -1 ; i = ne[i])
            {
                int j = e[i];
                if(d[j] == -1)
                {
                    d[j] = d[x] + 1;
                    q[++tt] = j;
                }
            }
        }
        return d[n];
    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        fill(h , -1);
        fill(d , -1);
        for(int i = 0 ; i < m ; i++)
        {
            int a = sc.nextInt();
            int b = sc.nextInt();
            add(a , b);
        }
        int res = bfs();
        System.out.println(res);
    }
}
```

