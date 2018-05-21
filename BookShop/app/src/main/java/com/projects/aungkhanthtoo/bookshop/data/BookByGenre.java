package com.projects.aungkhanthtoo.bookshop.data;

/**
 * Created by Lenovo on 2/20/2018.
 */

public class BookByGenre{

    private int id;

    private String codenumber;

    private String name;

    private int price;

    private Object publishingDate;

    private String description;

    private String image;

    private String savePdf;

    private Author author;

    private Genre genre;

    private Publisher publisher;

    public int getId() {
        return id;
    }

    public String getCodenumber() {
        return codenumber;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Object getPublishingDate() {
        return publishingDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getSavePdf() {
        return savePdf;
    }

    public Author getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public BookByGenre(int id, String codenumber, String name, int price, Object publishingDate, String description, String image, String savePdf, Author author, Genre genre, Publisher publisher) {
        this.id = id;
        this.codenumber = codenumber;
        this.name = name;
        this.price = price;
        this.publishingDate = publishingDate;
        this.description = description;
        this.image = image;
        this.savePdf = savePdf;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
    }

}
