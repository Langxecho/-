/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/24 8:09
 */

import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode implements Comparable<HuffmanNode> {//创建可比较的哈夫曼节点
    char data; //节点数据
    int frequency;  //节点频率
    HuffmanNode left, right; //定义左子树与右子树

    public HuffmanNode(char data, int frequency) { //初始化哈夫曼节点
        this.data = data;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(HuffmanNode o) { //定义比较器，用于和其他节点进行频率比较
        return this.frequency - o.frequency;
    }
}

public class HuffmanCoding {
    //哈希表，用于存储输入流代码
    private static HashMap<Character, String> huffmanCodes = new HashMap<>();

    public static void main(String[] args) {
        String inputFilePath = "input.txt"; //输入文件
        String compressedFilePath = "compressed.huff";//压缩文件路径
        String decompressedFilePath = "decompressed.txt";//解压文件路径

//        compressFile(inputFilePath, compressedFilePath);
        decompressFile(compressedFilePath, decompressedFilePath);
    }

    public static void compressFile(String inputFilePath, String compressedFilePath) {
        try {
            FileInputStream fis = new FileInputStream(inputFilePath);//创建文件输入流
            int[] frequency = new int[256]; // 定义频率表，256是因为这里假设使用asc码，存储256个字符

            //读取所有的字符byte,将其转化为char，便于统计
            int bytesRead;
            while ((bytesRead = fis.read()) != -1) {
                char c = (char) bytesRead;
                frequency[c]++;//统计字符的频率
            }
            //创建优先队列
            PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
            for (int i = 0; i < frequency.length; i++) {
                //将字符频率表中每个字符的频率转化为哈夫曼节点（节点含该字符以及字符的频率）
                if (frequency[i] > 0) {
                    priorityQueue.offer(new HuffmanNode((char) i, frequency[i]));
                }
            }
            //生成哈夫曼树
            HuffmanNode root = buildHuffmanTree(priorityQueue);
            //生成对应的哈夫曼编码
            generateHuffmanCodes(root, "", huffmanCodes);

            //创建文件输出流
            FileOutputStream fos = new FileOutputStream(compressedFilePath);
            //创建包装的对象输出流
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //将哈夫曼表直接输出
            oos.writeObject(huffmanCodes);

            fis = new FileInputStream(inputFilePath);//重新获取输入文件
            //创建缓冲字符
            StringBuilder encodedText = new StringBuilder();
            //读取输入文件中的每一个文字，在哈夫曼表中进行查找，并且将结果拼接成一整个字符串
            while ((bytesRead = fis.read()) != -1) {
                char c = (char) bytesRead;
                encodedText.append(huffmanCodes.get(c));
            }

            writeEncodedText(encodedText.toString(), fos);

            fis.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //构建哈夫曼树
    private static HuffmanNode buildHuffmanTree(PriorityQueue<HuffmanNode> priorityQueue) {
        //遍历优先队列
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();//取出队列中频率最小的，作为左子树
            HuffmanNode right = priorityQueue.poll();//取出队列中频率最小的，作为右子树
            //构建一个新的哈夫曼节点，频率为两个子节点相加，data数据并不重要，设置为0
            HuffmanNode internalNode = new HuffmanNode('\0', left.frequency + right.frequency);
            //将刚才的两个节点设置为新构造节点的两个子叶
            internalNode.left = left;
            internalNode.right = right;
            //将构造的节点入队，继续循环
            priorityQueue.offer(internalNode);
        }
        //只剩下根节点时便得到了我们想要的哈夫曼树，将其输出
        return priorityQueue.poll();
    }

    //生成哈夫曼编码
    private static void generateHuffmanCodes(HuffmanNode root, String code, HashMap<Character, String> huffmanCodes) {
        if (root == null) {
            return;
        }//节点为空，返回

        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.data, code);
        }//如果当前是叶子节点，将该节点代表的字符以及对应的编码记入

        //根据左右子树的状况不断递归，直到获取到每个叶子节点所代表的字符与编码
        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }
        //将哈夫曼编码后的二进制码以8位字节的形式写入文件当中
    private static void writeEncodedText(String encodedText, FileOutputStream fos) throws IOException {
        int index = 0;
        //8位二进制为一个单位，将其转化为10进制的一个数，写入文件当中
        while (index + 8 <= encodedText.length()) {
            int b = Integer.parseInt(encodedText.substring(index, index + 8), 2);
            fos.write(b);
            index += 8;
        }
        //不满足8位的数，需要进行补齐8位，然后再将其转换
        if (index < encodedText.length()) {
            int remainingBits = encodedText.length() - index;
            int b = Integer.parseInt(encodedText.substring(index), 2) << (8 - remainingBits);
            fos.write(b);
        }
    }

    public static void decompressFile(String compressedFilePath, String decompressedFilePath) {
        try {
            FileInputStream fis = new FileInputStream(compressedFilePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            //通过对象流直接得到经过编码的哈夫曼代码对象
            huffmanCodes = (HashMap<Character, String>) ois.readObject();

            //根据读到的代码重建二叉树
            HuffmanNode root = buildHuffmanTreeFromCodes(huffmanCodes);

            //获取输出流
            FileOutputStream fos = new FileOutputStream(decompressedFilePath);

            int bit;
            HuffmanNode current = root;
            while ((bit = fis.read()) != -1) {
                for (int i = 7; i >= 0; i--) {
                    int mask = 1 << i;
                    int bitValue = (bit & mask) == 0 ? 0 : 1;

                    if (bitValue == 0) {
                        current = current.left;
                    } else {
                        current = current.right;
                    }

                    if (current.left == null && current.right == null) {
                        fos.write(current.data);
                        current = root;
                    }
                }
            }

            fis.close();
            ois.close();
            fos.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static HuffmanNode buildHuffmanTreeFromCodes(HashMap<Character, String> huffmanCodes) {
        HuffmanNode root = new HuffmanNode('\0', 0);

        for (var entry : huffmanCodes.entrySet()) {
            char c = entry.getKey();
            String code = entry.getValue();

            HuffmanNode current = root;
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '0') {
                    if (current.left == null) {
                        current.left = new HuffmanNode('\0', 0);
                    }
                    current = current.left;
                } else {
                    if (current.right == null) {
                        current.right = new HuffmanNode('\0', 0);
                    }
                    current = current.right;
                }
            }
            current.data = c;
        }

        return root;
    }
}

