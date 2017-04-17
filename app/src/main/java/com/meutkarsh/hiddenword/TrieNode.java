package com.meutkarsh.hiddenword;

import java.util.HashMap;

/**
 * Created by USER on 3/20/2017.
 */

public class TrieNode {
    private HashMap< Character, TrieNode > children;
    private boolean isWord;

    public TrieNode(){
        children = new HashMap<>();
        isWord = false;
    }

    public HashMap< Character, TrieNode > getChildren(){
        return children;
    }

    public boolean isWord(String s){
        TrieNode current = this;
        int i, n = s.length();
        char c;
        for(i = 0; i < n; i++){
            c = s.charAt(i);
            HashMap< Character, TrieNode > child = current.getChildren();
            if(!child.containsKey(c)){
                return false;
            }else{
                current = child.get(c);
            }
        }
        return current.isWord;
    }

    public boolean isPrefix(String s){
        TrieNode current = this;
        int i, n = s.length();
        char c;
        for(i = 0; i < n; i++){
            c = s.charAt(i);
            HashMap< Character, TrieNode > child = current.getChildren();
            if(!child.containsKey(c)){
                return false;
            }else{
                current = child.get(c);
            }
        }
        return true;
    }

    public void add(String s){
        int n = s.length();
        TrieNode temp=this;//to store root

        for(int i = 0; i < n; i++){
            HashMap< Character, TrieNode > child = temp.getChildren();
            char  c = s.charAt(i);
            if(child.containsKey(c)){
                temp = child.get(c);
            }else{
                TrieNode new_node = new TrieNode();
                child.put(c, new_node);
                temp = new_node;
            }
        }
        temp.isWord = true;
    }
}
