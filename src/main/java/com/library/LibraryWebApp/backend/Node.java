package com.library.LibraryWebApp.backend;

public class Node {
    Book data;
    Node left;
    Node right;

    public Node(Book a){
        data = a;
        left=null;
        right = null;
    }

    public String getData() {
        return data.getIsbn();
    }
    public Book getBook(){
        return data;
    }
}
