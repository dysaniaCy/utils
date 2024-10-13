package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.collection.ListUtil;
import com.yiyouliao.autoprod.liaoyuan.entity.datastruchure.ShootingStyleDate;
import com.yiyouliao.autoprod.liaoyuan.entity.datastruchure.TreeNode;
import com.yiyouliao.autoprod.liaoyuan.entity.vo.ShootingStyleMapVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiedong
 * @dateTime: 2023/11/1 17:10
 **/
@Data
public class TreeUtil<T> {

    private  TreeNode<T> rootNode = new TreeNode<>();

    private Map<String, TreeNode<T>> nodeMap = new HashMap<>();

    private void add(TreeNode<T> parentNode, List<T> data, int index) {
        if (index >= data.size()) {
            return;
        }
        T value = data.get(index);
        String newKey = value + getParentValue(data, index);
        TreeNode<T> currentNode = nodeMap.get(newKey);

        if (currentNode == null) {
            currentNode = createNode(value);
            currentNode.setLevel(index + 1);
            nodeMap.put(newKey, currentNode);
            parentNode.addChild(currentNode);
        }

        add(currentNode, data, index + 1);
    }

    public TreeNode<T> getTree() {
        return rootNode;
    }
    public String getParentValue(List<T> data, int index) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < index; i++) {
            result.append(data.get(i));
        }
        return result.toString();
    }


    public void add(List<List<T>> dataList) {
        for (List<T> data : dataList) {
            add(rootNode, data, 0);
        }
    }

    private TreeNode<T> createNode(T value) {
        TreeNode<T> node = new TreeNode<>();
        node.setValue(value);
        return node;
    }

    public static void main(String[] args) {
        TreeUtil treeUtil = new TreeUtil();
        List<List<String>> data = new ArrayList<>();
        data.add(ListUtil.of("PANORAMA", "IMMOBILIZATION", "IMMOBILIZATION", "LEFT", "url1"));
        data.add(ListUtil.of("PANORAMA", "IMMOBILIZATION", "LIFTER", "LEFT", "url2"));
        data.add(ListUtil.of("PANORAMA", "IMMOBILIZATION", "LEFT_TO_RIGHT", "RIGHT", "url3"));

        TreeNode<String> rootNode1 = treeUtil.getRootNode();
        ShootingStyleDate shootingStyleDate = new ShootingStyleDate();
        ShootingStyleMapVO treeToList = shootingStyleDate.getTreeToList();

        System.out.println(treeToList);
    }
}