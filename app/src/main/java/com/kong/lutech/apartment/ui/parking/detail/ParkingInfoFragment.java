package com.kong.lutech.apartment.ui.parking.detail;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.model.CarImageParkingInfo;
import com.kong.lutech.apartment.model.CctvLog;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.utils.Optional;
import com.kong.lutech.apartment.view.ZoomableImageView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.Single;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingInfoFragment extends RxFragment {
    private final String TAG = getClass().getSimpleName();

    private ZoomableImageView ivThumbnail;
    private TextView tvCarNumber, tvLocation, tvDate;

    private Car car;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_parking_info, container, false);

        ivThumbnail = view.findViewById(R.id.ivThumbnail);
        ivThumbnail.setTouchEnabled(false);
        tvCarNumber = view.findViewById(R.id.tvCarNumber);
        tvLocation = view.findViewById(R.id.tvCarLocation);
        tvDate = view.findViewById(R.id.tvDate);

        car = getArguments().getParcelable("Car");

        tvCarNumber.setText(car.getNumber());


        /*ivThumbnail.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Bitmap parkingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.parking);
            ivThumbnail.setImageBitmap(parkingBitmap);
            ivThumbnail.setFocus(ivThumbnail.getWidth(), ivThumbnail.getHeight());

            List<Point> pins = new ArrayList<>();
            pins.add(new Point(parkingBitmap.getWidth() / 2 - 200, parkingBitmap.getHeight() / 2 - 200));
            ivThumbnail.setPins(parkingBitmap, pins, false);

            ivThumbnail.setTranslation(ivThumbnail.getWidth() / 2 + pins.get(0).x * -1, ivThumbnail.getHeight() / 2 + pins.get(0).y * -1);
            //ivThumbnail.setTranslation(10, 10);
        });*/


        loadCctvLog();

        return view;
    }



    class ParkingImageData {
        Bitmap bitmap;
        CarImageParkingInfo carImageParkingInfo;

        public ParkingImageData(Bitmap bitmap, CarImageParkingInfo carImageParkingInfo) {
            this.bitmap = bitmap;
            this.carImageParkingInfo = carImageParkingInfo;
        }
    }

    class LoadCctvLogData {
        Bitmap baseBitmap;
        ParkingImageData parkingImageData;

        public LoadCctvLogData(Bitmap baseBitmap, ParkingImageData parkingImageData) {
            this.baseBitmap = baseBitmap;
            this.parkingImageData = parkingImageData;
        }
    }

    private void loadCctvLog() {

        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();

            final String imageURL = "https://smap.kong-tech.com/api/apartments/" + mobile.getApartmentId() + "/cars/" + car.getSequence() + "/parking-image?dong=" + mobile.getDongSequence() + "&home=" + mobile.getHomeSequence();

            /*Single.unsafeCreate(new Single<Optional<Bitmap>>() {
                @SuppressLint("CheckResult")
                @Override
                protected void subscribeActual(SingleObserver<? super Optional<Bitmap>> observer) {
                    Log.e(TAG, "subscribe");
                    //Glide.with(getActivity()).asBitmap().load("https://www.alambassociates.com/wp-content/uploads/2016/10/maxresdefault.jpg").submit().get()
                    Glide.with(getActivity()).asBitmap().load("https://www.alambassociates.com/wp-content/uploads/2016/10/maxresdefault.jpg").listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException ex, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Log.e(TAG, "onLoadFailed");
                            observer.onError(ex);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            Log.e(TAG, "onResourceReady");
                            //final Bitmap bitmap = ZoomableImageView.getBitmapFromURL(parkImage.getPhotoPath(), null);
                            if (resource == null) {
                                observer.onError(new NullPointerException());
                                return false;
                            }

                            observer.onSuccess(new Optional<>(resource));
                            return false;
                        }
                    });
                }
            }).subscribe(bitmapOptional -> {
                Log.e(TAG, "onSuccess : " + bitmapOptional.get());
            },throwable -> {
                throwable.printStackTrace();
            });*/


            /*final Single<ParkingImageData> singleParkingImageData =
                    Single.just(Glide.with(getActivity()).asBitmap().load(new GlideUrl(imageURL, new LazyHeaders.Builder().addHeader("Authorization", Config.getAccessToken()).build())).submit())
                            .subscribeOn(Schedulers.io())
                            .map(Future::get)
                            .map(Optional::new)
                            .onErrorReturn(throwable -> new Optional<>(null))
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(bitmapOptional -> {
                                if (bitmapOptional.isEmpty()) {
                                    return Single.just(new ParkingImageData(null, null));
                                } else {
                                    return ParkingRestUtil.getIntsance(getActivity()).getCarParkingInfo(Config.getAccessToken(), mobile.getApartmentId(), car.getSequence(), mobile.getDongSequence(), mobile.getHomeSequence())
                                            .flatMap(carImageParkingInfoDataResponse -> {
                                                if (carImageParkingInfoDataResponse.isSuccessful() && carImageParkingInfoDataResponse.body() != null && carImageParkingInfoDataResponse.body().getData() != null) {
                                                    return Single.just(carImageParkingInfoDataResponse.body().getData())
                                                            .map(carImageParkingInfo -> new ParkingImageData(bitmapOptional.get(), carImageParkingInfo));
                                                } else {
                                                    return Single.just(new ParkingImageData(bitmapOptional.get(), null));
                                                }
                                            })
                                            .onErrorReturnItem(new ParkingImageData(bitmapOptional.get(), null));
                                }
                            })
                            .doOnSuccess(parkingImageData -> Log.e(TAG, "singleParkingImageData doOnSuccess"));*/


            final Single<Optional<Bitmap>> singleOptionalBitmap = ApartmentRestUtil.getIntsance(getActivity())
                    .getLatestCctvLog(Config.getAccessToken(), mobile.getApartmentId(), car.getCarId())
                    .compose(bindToLifecycle())
                    .doOnSuccess(cctvLogResponse -> {
                        if (cctvLogResponse.isSuccessful()) {
                            Log.d(TAG, "Request CctvLogs Success");

                            final CctvLog cctvLog = cctvLogResponse.body().getCctvLog();
                            if (cctvLog != null) {
                                if (!TextUtils.isEmpty(cctvLog.getCarNumber()))
                                    tvCarNumber.setText(cctvLog.getCarNumber());

                                final StringBuilder locationStr = new StringBuilder();
                                if (!TextUtils.isEmpty(cctvLog.getParkName())) {
                                    locationStr.append(cctvLog.getParkName());
                                }
                                if (!TextUtils.isEmpty(cctvLog.getParkName()) && !TextUtils.isEmpty(cctvLog.getCctvName())) {
                                    locationStr.append(", ");
                                    locationStr.append(cctvLog.getCctvName());
                                }
                                if (!TextUtils.isEmpty(locationStr))
                                    tvLocation.setText(locationStr.toString());

                                if (cctvLog.getDate() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  a hh:mm", Locale.ENGLISH);
                                    tvDate.setVisibility(View.VISIBLE);
                                    tvDate.setText(dateFormat.format(cctvLog.getDate()));
                                } else {
                                    tvDate.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            failLoadParkingInfo();
                        }
                    })
                    .map(latestCctvLogResponse -> {

                        if (latestCctvLogResponse.body().getCctvLog().getPicture() != null) {
                            byte[] decodedString = Base64.decode(latestCctvLogResponse.body().getCctvLog().getPicture(), Base64.DEFAULT);
                            final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            return new Optional<>(decodedByte);
                        } else {
                            return new Optional<Bitmap>(null);
                        }
                    })
                    .onErrorReturnItem(new Optional<>(null))
                    .doOnSuccess(parkingImageData -> Log.e(TAG, "singleOptionalBitmap doOnSuccess"));

            final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "정보 불러오는 중...", true, false);

            singleOptionalBitmap.doFinally(dialog::dismiss).subscribe(bitmapOptional -> {
                if (!bitmapOptional.isEmpty()) {
                    ivThumbnail.setImageBitmap(bitmapOptional.get());
                    ivThumbnail.focusCenter();
                }
            });



            /*Single.zip(singleOptionalBitmap, singleParkingImageData,
                    (bitmapOptional, parkingImageData) -> new LoadCctvLogData(bitmapOptional.isEmpty() ? null : bitmapOptional.get(), parkingImageData))
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnEvent((loadCctvLogData, throwable) -> dialog.dismiss())
                    .subscribe(loadCctvLogData -> {
                        ivThumbnail.setFocus(ivThumbnail.getWidth(), ivThumbnail.getHeight());

                        if ( loadCctvLogData.parkingImageData.bitmap != null ) {

                            Bitmap bitmap = loadCctvLogData.parkingImageData.bitmap;

                            if ( loadCctvLogData.parkingImageData.carImageParkingInfo != null && loadCctvLogData.parkingImageData.carImageParkingInfo.getCctv() != null) {
                                bitmap = ZoomableImageView.mergeCctvLocationBitmap(bitmap, loadCctvLogData.parkingImageData.carImageParkingInfo.getCctv());
                            }

                            ivThumbnail.setImageBitmap(bitmap);
                            ivThumbnail.setMapSize(bitmap.getWidth(), bitmap.getHeight());

                            ivThumbnail.clear();

                            if ( loadCctvLogData.parkingImageData.carImageParkingInfo != null && loadCctvLogData.parkingImageData.carImageParkingInfo.getCctv() != null) {
                                final ParkImageParkingInfo.Cctv cctv = loadCctvLogData.parkingImageData.carImageParkingInfo.getCctv();
                                if ( cctv.getxPosition() >= 0 && cctv.getyPosition() >= 0 ) {
                                    ivThumbnail.setCctvLocation(cctv.getxPosition(), cctv.getyPosition());
                                    return;
                                }
                            }
                            ivThumbnail.focusCenter();

                        }
                        else if ( loadCctvLogData.baseBitmap != null ) {
                            ivThumbnail.setImageBitmap(loadCctvLogData.baseBitmap);
                            ivThumbnail.focusCenter();
                        }
                    });*/



            /*ParkingRestUtil.getIntsance(getActivity()).getCarParkingInfo(Config.getAccessToken(), mobile.getApartmentId(), car.getSequence(), mobile.getDongSequence(), mobile.getHomeSequence())
                    .filter(preferedParkingInfoResponse -> {
                        if (preferedParkingInfoResponse.isSuccessful() && preferedParkingInfoResponse.body() != null && preferedParkingInfoResponse.body().getData() != null) {
                            return true;
                        } else {
                            throw new Error("차량 주차 정보 조회에 실패하였습니다.");
                        }
                    })
                    .map(parkImageParkingInfoDataResponse -> parkImageParkingInfoDataResponse.body().getData())
                    .subscribe(parkImageParkingInfo -> {

                        ivThumbnail.setFocus(ivThumbnail.getWidth() / 2, ivThumbnail.getHeight() / 2);

                        new Thread(() -> {

                            final ParkImageParkingInfo.Cctv cctv = parkImageParkingInfo.getCctv();
                            if (cctv == null) return;

                            final Bitmap bitmap = ZoomableImageView.mergeCctvLocationBitmap(ZoomableImageView.getBitmapFromURL(imageURL, Config.getAccessToken()), cctv);
                            getActivity().runOnUiThread(() -> {
                                ivThumbnail.setImageBitmap(bitmap);
                                ivThumbnail.setMapSize(bitmap.getWidth(), bitmap.getHeight());

                                ivThumbnail.clear();

                                if (cctv.getxPosition() < 0 || cctv.getyPosition() < 0 ) {
                                    final ArrayList<Integer> mapLocation = parkImageParkingInfo.getMapLocation();
                                    if (mapLocation.size() == 4) {
                                        ivThumbnail.setInsets(new float[]{ mapLocation.get(0), mapLocation.get(1), mapLocation.get(2), mapLocation.get(3) });
                                    }
                                } else {
                                    ivThumbnail.setTranslation(cctv.getxPosition() * -1 + ivThumbnail.getWidth() / 2, cctv.getyPosition() * -1 + ivThumbnail.getHeight() / 2);
                                    ivThumbnail.setScale(3f, ivThumbnail.getWidth() / 2, ivThumbnail.getHeight() / 2, false);
                                }
                            });
                        }).start();
                    }, Throwable::printStackTrace);*/

            /*ApartmentRestUtil.getIntsance(getActivity())
                    .getLatestCctvLog(Config.getAccessToken(), mobile.getApartmentId(), car.getCarId())
                    .compose(bindToLifecycle())
                    .subscribe(cctvLogResponse -> {
                        if (cctvLogResponse.isSuccessful()) {
                            Log.d(TAG, "Request CctvLogs Success");

                            final CctvLog cctvLog = cctvLogResponse.body().getCctvLog();
                            if (cctvLog != null) {
                                if (!TextUtils.isEmpty(cctvLog.getCarNumber())) tvCarNumber.setText(cctvLog.getCarNumber());

                                final StringBuilder locationStr = new StringBuilder();
                                if (!TextUtils.isEmpty(cctvLog.getParkName())) {
                                    locationStr.append(cctvLog.getParkName());
                                }
                                if (!TextUtils.isEmpty(cctvLog.getParkName()) && !TextUtils.isEmpty(cctvLog.getCctvName())) {
                                    locationStr.append(", ");
                                    locationStr.append(cctvLog.getCctvName());
                                }
                                if (!TextUtils.isEmpty(locationStr)) tvLocation.setText(locationStr.toString());

                                if (cctvLog.getDate() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  a hh:mm", Locale.ENGLISH);
                                    tvDate.setVisibility(View.VISIBLE);
                                    tvDate.setText(dateFormat.format(cctvLog.getDate()));
                                } else {
                                    tvDate.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            failLoadParkingInfo();
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        failLoadParkingInfo();
                    });*/
        }
    }

    private void failLoadParkingInfo() {
        new AlertDialog.Builder(getActivity()).setMessage("주차 정보를 읽어오는데 실패하였습니다.").setPositiveButton("확인", null).show();
    }
}
