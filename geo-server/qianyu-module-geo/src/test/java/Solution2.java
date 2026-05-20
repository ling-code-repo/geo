import java.util.*;

public class Solution2 {
    public int[] mergeSortedArray(int[] A, int[] B) {
        int n = A.length, m = B.length;
        int[] result = new int[n + m];
        
        int i = 0, j = 0, k = 0;
        
        // 双指针合并
        while (i < n && j < m) {
            if (A[i] <= B[j]) {
                result[k++] = A[i++];
            } else {
                result[k++] = B[j++];
            }
        }
        
        // 处理剩余元素
        while (i < n) {
            result[k++] = A[i++];
        }
        while (j < m) {
            result[k++] = B[j++];
        }
        
        return result;
    }
    
    // 测试
    public static void main(String[] args) {
        Solution2 sol = new Solution2();
        
        // 样例 1
        int[] A1 = {1};
        int[] B1 = {1};
        System.out.println(Arrays.toString(sol.mergeSortedArray(A1, B1)));
        // 输出: [1, 1]
        
        // 样例 2
        int[] A2 = {1, 2, 3, 4};
        int[] B2 = {2, 4, 5, 6};
        System.out.println(Arrays.toString(sol.mergeSortedArray(A2, B2)));
        // 输出: [1, 2, 2, 3, 4, 4, 5, 6]
    }
}