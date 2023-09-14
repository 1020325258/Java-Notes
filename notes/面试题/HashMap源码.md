---
typora-copy-images-to: imgs
---



数组+单向链表+双向链表+红黑树



```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    /**
    为什么要先定义变量来将table赋值给tab？是为了性能考虑
    将table赋值给tab，每次去栈里边取tab比每次去堆里取table速度要快
    */
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //这里将table赋值给tab
    if ((tab = table) == null || (n = tab.length) == 0)
        //进行初始化数组操作,tab=resize()是对tab进行扩容 
        n = (tab = resize()).length;
    /**
    这里通过&操作计算下标i：
    HashMap如果初始化传入的n是2的次方，那么会自动将n转化为大于等于n的2的次方数
    n-1那么转为二进制之后k为肯定都为1（前提是n需要是2的n次方），其余位都是0
    那么与操作之后，只会取hash值的后k位，这样就可以保证i不会发生数组越界，并且这样取i的位置比较平衡，不会集中到某几位
    */
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}

static final int hash(Object key) {
    int h;
    /**
       在计算hash值的时候，根据key的hashCode来计算，并不直接取hashCode：
    先将hashCode赋值给h，之后再让 h 与 h>>>16 进行异或运算，这个是什么意思呢？
    是为了让hash值的计算更加平衡，hash是int类型，也就是32为
    h >>> 16 是无符号右移16位，让h的高16位与低16位都参与运算，这样计算出来的hash值更加平衡
    # 在上边 putVal() 中计算下标 i 的时候，是让 i = (n-1)&hash，这样计算下标的时候只取
    了hash的后k位，所以让让hash值的高16位与低16位都参与运算，为了在取hash的低位的时候更加平衡
    */
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

