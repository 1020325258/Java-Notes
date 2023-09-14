

```c
#include <iostream>
#include <algorithm>
using namespace std;

struct Range
{
	int l , r;
	bool operator< (const Range &W)
	{
		// 重载运算符 < ，根据左端点进行排序 
		return l < W.l;	
	}	
}range[10];

int main()
{
	for(int i = 1 ; i <= 10 ; i++)
	{
		range[i] = {10 - i , i};
	}
	
	// 排序 
	sort(range , range + 10);
	
	for(int i = 1 ; i <= 10 ; i++)
	{
		cout << range[i].l << ' ' << range[i].r << endl;
	}
	return 0;
}
```

