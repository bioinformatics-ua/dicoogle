package pt.ua.dicoogle.sdk.mlprovider;

import java.io.InputStream;

public class MLImageDataset extends MLDataset {
    Iterable<InputStream> images;
    Iterable<String> labels;
}
