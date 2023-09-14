

### 831. KMP字符串                           

给定一个模式串S，以及一个模板串P，所有字符串中只包含大小写英文字母以及阿拉伯数字。

模板串P在模式串S中多次作为子串出现。

求出模板串P在模式串S中所有出现的位置的起始下标。

**输入格式**

第一行输入整数N，表示字符串P的长度。

第二行输入字符串P。

第三行输入整数M，表示字符串S的长度。

第四行输入字符串S。

**输出格式**

共一行，输出所有出现位置的起始下标（下标从0开始计数），整数之间用空格隔开。

**数据范围**

1≤N≤1051≤N≤105
1≤M≤1061≤M≤106

**输入样例：**

```
3
aba
5
ababa

```

**输出样例：**

```
0 2
```





```java
import java.util.*;
import java.io.*;
public class Main{
    public static void main(String[] args) throws IOException{
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        int n = Integer.parseInt(bf.readLine());
        char[] p = (" " + bf.readLine()).toCharArray(); 
        int m = Integer.parseInt(bf.readLine());
        char[] s = (" " + bf.readLine()).toCharArray();
        int[] next = new int[n+1];
        //求next数组
        for(int i = 2 , j = 0 ; i <= n ; i++)
        {
            // 如果不加j!=0为陷入死循环
            while(j != 0 && p[i] != p[j+1]) j = next[j];
            if(p[i] == p[j+1]) j++;
            next[i] = j;
        }
        
        //kmp匹配
        for(int i = 1 , j = 0 ; i <= m ; i++)
        {
            while(j != 0 && s[i] != p[j+1]) j = next[j];
            if(s[i] == p[j+1]) j++;
            if(j == n)
            {
                bw.write(i - n + " ");
                j = next[j];
            }
        }
        bw.flush();
        bw.close();
        bf.close();
        
    }
}
```

