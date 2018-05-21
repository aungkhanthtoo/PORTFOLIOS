package com.projects.aungkhanthtoo.bookshop.network;

import com.projects.aungkhanthtoo.bookshop.data.GenresResponse;
import com.projects.aungkhanthtoo.bookshop.data.BooksResponse;
import com.projects.aungkhanthtoo.bookshop.data.AuthorsResponse;
import com.projects.aungkhanthtoo.bookshop.data.PublishersResponse;
import com.projects.aungkhanthtoo.bookshop.data.SearchResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lenovo on 2/13/2018.
 */

public class RetrofitHelper {


    private ApiService mApiService;
    private static final String BASE_URL = "http://student.newwestgroup.org/bookstore/api/";

    public RetrofitHelper() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = retrofit.create(ApiService.class);
    }

    public Call<BooksResponse> getBooks(){
        return mApiService.getAllBooks();
    }

    public Call<GenresResponse> getGenres(){
        return mApiService.getAllGenres();
    }

    public Call<AuthorsResponse> getAuthors(){
        return mApiService.getAllAuthors();
    }

    public Call<PublishersResponse> getPublishers(){
        return mApiService.getAllPublishers();
    }

    public Call<SearchResponse> getSearchBooks(String keyword){
        return mApiService.searchAllBooks(keyword);
    }

    public Call<ResponseBody> getPdf(String url){
        return mApiService.downloadPdf(url);
    }

    public Call<BooksResponse> getBooksByGenre(int genreId){
        return mApiService.getBooksByGenre(genreId);
    }

    public Call<BooksResponse> getBooksByAuthor(int authorId){
        return mApiService.getBooksByAuthor(authorId);
    }

    public Call<BooksResponse> getBooksByPublisher(int publisherId){
        return mApiService.getBooksByPublisher(publisherId);
    }

    public Call<BooksResponse> login(String email, String pwd){
        return mApiService.login(email, pwd);
    }

    public Call<BooksResponse> logout(){
        return mApiService.logout();
    }

    public Call<BooksResponse> createGenre(String name){
        return mApiService.createGenre(name);
    }

    public Call<BooksResponse> createAuthor(String name){
        return mApiService.createAuthor(name);
    }

    public Call<BooksResponse> createPublisher(String name){
        return mApiService.createPublisher(name);
    }

    public Call<BooksResponse> editGenre(int id, String name){
        return mApiService.editGenre(id, name);
    }

    public Call<BooksResponse> editAuthor(int id, String name){
        return mApiService.editAuthor(id, name);
    }

    public Call<BooksResponse> editPublisher(int id, String name){
        return mApiService.editPublisher(id, name);
    }

    public Call<BooksResponse> deleteBook(int id){
        return mApiService.deleteBook(id);
    }

    public Call<BooksResponse> createBook(
            RequestBody codeno,
            RequestBody name,
            RequestBody description,
            RequestBody price,
            RequestBody edition,
            MultipartBody.Part image,
            MultipartBody.Part pdf,
            RequestBody authorId,
            RequestBody genreId,
            RequestBody publisherId
    ){
        return mApiService.createBook(codeno, name, description, price, edition, image, pdf, authorId, genreId, publisherId);
    }

    public Call<BooksResponse> updateBook(
            RequestBody id,
            RequestBody codeno,
            RequestBody name,
            RequestBody description,
            RequestBody price,
            RequestBody edition,
            MultipartBody.Part image,
            MultipartBody.Part pdf,
            RequestBody authorId,
            RequestBody genreId,
            RequestBody publisherId
    ){
        return mApiService.updateBook(id, codeno, name, description, price, edition, image, pdf, authorId, genreId, publisherId);
    }
}
