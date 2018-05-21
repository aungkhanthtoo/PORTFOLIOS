package com.projects.aungkhanthtoo.bookshop.network;

import com.projects.aungkhanthtoo.bookshop.data.AuthorsResponse;
import com.projects.aungkhanthtoo.bookshop.data.BooksResponse;
import com.projects.aungkhanthtoo.bookshop.data.GenresResponse;
import com.projects.aungkhanthtoo.bookshop.data.PublishersResponse;
import com.projects.aungkhanthtoo.bookshop.data.SearchResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by Lenovo on 2/13/2018.
 */

public interface ApiService {

    @GET("get_all_book")
    Call<BooksResponse> getAllBooks();

    @GET
    Call<ResponseBody> downloadPdf(@Url String fileUrl);

    @GET("get_all_genre")
    Call<GenresResponse> getAllGenres();

    @GET("get_all_author")
    Call<AuthorsResponse> getAllAuthors();

    @GET("get_all_publisher")
    Call<PublishersResponse> getAllPublishers();

    @FormUrlEncoded
    @POST("search")
    Call<SearchResponse> searchAllBooks(
            @Field(value = "keyword", encoded = true) String keyword
    );

    @FormUrlEncoded
    @POST("get_book_by_genre")
    Call<BooksResponse> getBooksByGenre(
            @Field(value = "genre_id", encoded = true) int genreId
    );

    @FormUrlEncoded
    @POST("get_book_by_author")
    Call<BooksResponse> getBooksByAuthor(
            @Field(value = "author_id", encoded = true) int authorId
    );

    @FormUrlEncoded
    @POST("get_book_by_publisher")
    Call<BooksResponse> getBooksByPublisher(
            @Field(value = "publisher_id", encoded = true) int publisherId
    );

    @FormUrlEncoded
    @POST("login")
    Call<BooksResponse> login(
            @Field(value = "email", encoded = true) String email,
            @Field(value = "pw", encoded = true) String password
    );

    @POST("logout")
    Call<BooksResponse> logout();

    @FormUrlEncoded
    @POST("create_genre")
    Call<BooksResponse> createGenre(@Field("name") String name);

    @FormUrlEncoded
    @POST("create_author")
    Call<BooksResponse> createAuthor(@Field("name") String name);

    @FormUrlEncoded
    @POST("create_publisher")
    Call<BooksResponse> createPublisher(@Field("name") String name);

    @FormUrlEncoded
    @POST("edit_genre")
    Call<BooksResponse> editGenre(@Field("id") int id, @Field("name") String name);

    @FormUrlEncoded
    @POST("edit_author")
    Call<BooksResponse> editAuthor(@Field("id") int id, @Field("name") String name);

    @FormUrlEncoded
    @POST("edit_publisher")
    Call<BooksResponse> editPublisher(@Field("id") int id, @Field("name") String name);

    @FormUrlEncoded
    @POST("book_delete")
    Call<BooksResponse> deleteBook(@Field("id") int id);

    @Multipart
    @POST("book_create")
    Call<BooksResponse> createBook(@Part("codeno") RequestBody codeno,
                                   @Part("name") RequestBody name,
                                   @Part("desc") RequestBody description,
                                   @Part("price") RequestBody price,
                                   @Part("edition") RequestBody edition,
                                   @Part MultipartBody.Part image,
                                   @Part MultipartBody.Part pdf,
                                   @Part("author_id") RequestBody authorId,
                                   @Part("genre_id") RequestBody genreId,
                                   @Part("publisher_id") RequestBody publisherId);

    @Multipart
    @POST("book_update")
    Call<BooksResponse> updateBook(@Part("id") RequestBody id,
                                   @Part("codeno") RequestBody codeno,
                                   @Part("name") RequestBody name,
                                   @Part("desc") RequestBody description,
                                   @Part("price") RequestBody price,
                                   @Part("edition") RequestBody edition,
                                   @Part MultipartBody.Part image,
                                   @Part MultipartBody.Part pdf,
                                   @Part("author_id") RequestBody authorId,
                                   @Part("genre_id") RequestBody genreId,
                                   @Part("publisher_id") RequestBody publisherId);
}
