package pro.oncreate.easynet;

import java.util.ArrayList;

import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTaskCallback;

/**
 * Created by andrej on 04.10.16.
 */

public class NConfig {

    // Init
    //

    private static NConfig config;

    private NConfig() {
        //nBuilder = NBuilder.newInstance();
        //nMultipartBuilder = NMultipartBuilder;
    }

    public static NConfig getInstance() {
        if (config == null)
            config = new NConfig();
        return config;
    }

    // Default builders
    //

    private NBuilder nBuilder;
    private NMultipartBuilder nMultipartBuilder;

    private NBuilderDefaultListener defaultNBuilderListener;
    private NMultipartBuilderDefaultListener defaultNMultipartBuilderListener;

    public void setDefaultNBuilderListener(NBuilderDefaultListener defaultNBuilderListener) {
        this.defaultNBuilderListener = defaultNBuilderListener;
    }

    public NBuilder getDefaultNBuilder() {
        if (defaultNBuilderListener != null) {
            this.nBuilder = defaultNBuilderListener.defaultConfig(NBuilder.newInstance());
        }
        return this.nBuilder;
    }

    public NMultipartBuilder getDefaultNMultipartBuilder() {
        if (defaultNMultipartBuilderListener != null) {
            this.nMultipartBuilder = defaultNMultipartBuilderListener.defaultConfig(nMultipartBuilder);
        }
        return this.nMultipartBuilder;
    }

    public interface NBuilderDefaultListener {
        NBuilder defaultConfig(NBuilder nBuilder);
    }

    public interface NMultipartBuilderDefaultListener {
        NMultipartBuilder defaultConfig(NMultipartBuilder nBuilder);
    }

    // Default behaviour
    //

    // Success

    private OnSuccessDefaultListener onSuccessDefaultListener;

    public void setDefaultOnSuccessListener(OnSuccessDefaultListener onSuccessDefaultListener) {
        this.onSuccessDefaultListener = onSuccessDefaultListener;
    }

    public OnSuccessDefaultListener getOnSuccessDefaultListener() {
        return onSuccessDefaultListener;
    }

    public interface OnSuccessDefaultListener {
        boolean onSuccess(NResponseModel responseModel);
    }

    // Failed

    private OnFailedDefaultListener onFailedDefaultListener;

    public void setDefaultOnFailedListener(OnFailedDefaultListener onFailedDefaultListener) {
        this.onFailedDefaultListener = onFailedDefaultListener;
    }

    public OnFailedDefaultListener getOnFailedDefaultListener() {
        return onFailedDefaultListener;
    }

    public interface OnFailedDefaultListener {
        boolean onFailed(NRequestModel nRequestModel, NTaskCallback.Errors error);
    }

    // Errors

    private ArrayList<OnErrorDefaultListenerWithCode> onErrorDefaultListenersCollection = new ArrayList<>();
    private OnErrorDefaultListener onErrorDefaultListener;

    public ArrayList<OnErrorDefaultListenerWithCode> getOnErrorDefaultListenersCollection() {
        return onErrorDefaultListenersCollection;
    }

    public void addOnErrorDefaultListener(OnErrorDefaultListenerWithCode onErrorDefaultListener) {
        onErrorDefaultListenersCollection.add(onErrorDefaultListener);
    }

    public OnErrorDefaultListener getOnErrorDefaultListener() {
        return onErrorDefaultListener;
    }

    public void setOnErrorDefaultListener(OnErrorDefaultListener onErrorDefaultListener) {
        this.onErrorDefaultListener = onErrorDefaultListener;
    }

    public static abstract class OnErrorDefaultListenerWithCode extends OnErrorDefaultListener {

        private int code;

        public OnErrorDefaultListenerWithCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static abstract class OnErrorDefaultListener {

        public abstract void onError(NResponseModel responseModel);
    }
}

