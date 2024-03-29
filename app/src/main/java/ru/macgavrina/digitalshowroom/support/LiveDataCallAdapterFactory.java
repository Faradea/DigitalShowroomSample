//package ru.decathlon.digitalshowroom.support;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//
//import androidx.lifecycle.MutableLiveData;
//import retrofit2.Call;
//import retrofit2.CallAdapter;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
///**
// * http://innotech.vn
// * Created by Huynh Van Duc on 11/1/2018.
// */
//public final class LiveDataCallAdapterFactory extends CallAdapter.Factory {
//
//    private LiveDataCallAdapterFactory() {
//    }
//
//    public static LiveDataCallAdapterFactory create() {
//        return new LiveDataCallAdapterFactory();
//    }
//
//    @Override
//    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
//
//        if (getRawType(returnType) != MutableLiveData.class) {
//            return null;
//        }
//        if (!(returnType instanceof ParameterizedType)) {
//            throw new IllegalStateException("LiveData return type must be parameterized"
//                    + " as LiveData<Foo> or LiveData<? extends Foo>");
//        }
//        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
//
//        if (getRawType(innerType) != Response.class) {
//            // Generic type is not Response<T>. Use it for body-only adapter.
//            return new BodyCallAdapter<>(innerType);
//        }
//
//        // Generic type is Response<T>. Extract T and create the Response version of the adapter.
//        if (!(innerType instanceof ParameterizedType)) {
//            throw new IllegalStateException("Response must be parameterized"
//                    + " as Response<Foo> or Response<? extends Foo>");
//        }
//        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
//        return new ResponseCallAdapter<>(responseType);
//    }
//
//    private static final class BodyCallAdapter<R> implements CallAdapter<R, MutableLiveData<R>> {
//        private final Type responseType;
//
//        BodyCallAdapter(Type responseType) {
//            this.responseType = responseType;
//        }
//
//        @Override
//        public Type responseType() {
//            return responseType;
//        }
//
//        @Override
//        public MutableLiveData<R> adapt(final Call<R> call) {
//            return new MutableLiveData<R>() {
//                {
//                    call.enqueue(new Callback<R>() {
//                        @Override
//                        public void onResponse(Call<R> call, Response<R> response) {
//                            if (response.isSuccessful()) {
//                                postValue(response.body());
//                            } else {
//                                postValue(null);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<R> call, Throwable t) {
//                            postValue(null);
//                        }
//                    });
//                }
//
//                @Override
//                protected void onInactive() {
//                    call.cancel();
//                }
//            };
//        }
//    }
//
//    private static final class ResponseCallAdapter<R>
//            implements CallAdapter<R, MutableLiveData<Response<R>>> {
//        private final Type responseType;
//
//        ResponseCallAdapter(Type responseType) {
//            this.responseType = responseType;
//        }
//
//        @Override
//        public Type responseType() {
//            return responseType;
//        }
//
//        @Override
//        public MutableLiveData<Response<R>> adapt(final Call<R> call) {
//            return new MutableLiveData<Response<R>>() {
//                {
//                    call.enqueue(new Callback<R>() {
//                        @Override
//                        public void onResponse(Call<R> call, Response<R> response) {
//                            setValue(response);
//                        }
//
//                        @Override
//                        public void onFailure(Call<R> call, Throwable t) {
//                            setValue(null);
//                        }
//                    });
//                }
//
//                @Override
//                protected void onInactive() {
//                    call.cancel();
//                }
//            };
//        }
//    }
//}