/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.streamplify.benchmark;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.beryx.streamplify.shuffler.ShufflerImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class ShufflerView extends Application {
    private static final String COMMENT =
              "This visualization shows that the values produced by ShufflerImpl are not uniformly distributed, although the algorithm does a good job at hiding this.\n"
            + "Due to the way the shuffling is implemented, it is more probable to observe non-random patterns for indices at distances that are multiples of 256.\n"
            + "The indices used in the five columns of the visualization are arithmetic progressions with the following common differences: 1, 2^8, 2^16, 2^24, 2^32.\n"
            + "As long as the real shuffled values are diplayed, everything looks pretty random.\n"
            + "However, if starting from left we XOR each byte with its successor, non-random patterns become evident.\n"
            + "The number of stripes grows with the exponent used for the common difference in the arithmetic progressions.";

    private static class ByteArrayImage {
        private final BigInteger idxCount;
        private final ShufflerImpl shuffler = new ShufflerImpl(new Random(0));
        private Consumer<byte[]> transformation;

        ByteArrayImage(BigInteger idxCount) {
            this.idxCount = idxCount;
        }

        void resetTransformation() {
            this.transformation = null;
        }
        void setXorTransformation() {
            this.transformation = bytes -> {
                for(int i = 0; i < bytes.length - 1; i++) {
                    bytes[i] ^= bytes[i + 1];
                }
            };
        }
        void enableXorTransformation(boolean enabled) {
            if(enabled) setXorTransformation();
            else resetTransformation();
        }

        Image createImage(BigInteger startIndex, int count, long step) {
            int width = idxCount.subtract(BigInteger.ONE).bitLength();
            WritableImage img = new WritableImage(2 * width, 2 * count);
            PixelWriter pw  = img.getPixelWriter();
            for (int y = 0 ; y < count ; y++) {
                BigInteger idx = startIndex.add(BigInteger.valueOf(y * step));
                BigInteger shuffIdx = shuffler.getShuffledIndex(idx, idxCount);
                boolean[] shuffledBits = toBoolBits(shuffIdx, idxCount);
                for (int x = 0 ; x < width ; x++) {
                    Color color = shuffledBits[x] ? Color.BLACK : Color.WHITE;
                    pw.setColor(2 * x, 2 * y, color);
                    pw.setColor(2 * x + 1, 2 * y, color);
                    pw.setColor(2 * x, 2 * y + 1, color);
                    pw.setColor(2 * x + 1, 2 * y + 1, color);
                }
            }
            return img;
        }

        boolean[] toBoolBits(BigInteger index, BigInteger count) {
            int bitCount = count.subtract(BigInteger.ONE).bitLength();
            int wholeBytes = bitCount / 8;
            int restBits = bitCount % 8;
            int bytesLen = (bitCount + 7) / 8;

            byte[] bytes = index.toByteArray();
            if(bytes.length < bytesLen) {
                byte[] tmp = bytes;
                bytes = new byte[bytesLen];
                System.arraycopy(tmp, 0, bytes, bytesLen - tmp.length, tmp.length);
            }

            if(transformation != null) transformation.accept(bytes);

            boolean[] boolBits = new boolean[bitCount];
            if(restBits > 0) {
                System.arraycopy(toBoolBits(bytes[0], restBits), 0, boolBits, 0, restBits);
            }
            for(int i=0; i<wholeBytes; i++) {
                boolean[] boolByte = toBoolBits(bytes[i + bytesLen - wholeBytes], 8);
                System.arraycopy(boolByte, 0, boolBits, restBits + 8 * i, 8);
            }
            return boolBits;
        }

        static boolean[] toBoolBits(byte b, int bitCount) {
            boolean[] bits = new boolean[bitCount];
            for(int i=0; i<bitCount; i++) {
                bits[bitCount - 1 - i] = (((b >>  i) & 1) == 1);
            }
            return bits;
        }
    }

    private static class MainPane extends GridPane {
        static final int IMAGE_COUNT = 5;

        private final BigInteger startIndex;
        private final BigInteger idxCount;

        private final CheckBox ckTrafo = new CheckBox("Perform XOR with next byte");
        private final List<ByteArrayImage> bais = new ArrayList<>();
        private final List<ImageView> imageViews = new ArrayList<>();

        private MainPane(BigInteger startIndex, BigInteger idxCount) {
            this.startIndex = startIndex;
            this.idxCount = idxCount;

            configureLayout();

            ckTrafo.setOnAction(ev -> refreshImages());
            add(ckTrafo, 0, 0, 5, 1);

            initByteArrayImages();

            TextArea textArea = new TextArea(COMMENT);
            textArea.setMaxHeight(-1);
            textArea.setWrapText(true);
            textArea.setEditable(false);

            add(textArea, 0, 3, 5, 1);
        }

        private void configureLayout() {
            setHgap(60);
            setVgap(10);
            setPadding(new Insets(20, 60, 20, 60));

            RowConstraints row0 = new RowConstraints(20);
            row0.setVgrow(Priority.NEVER);

            RowConstraints row1 = new RowConstraints(20);
            row1.setVgrow(Priority.NEVER);

            RowConstraints row2 = new RowConstraints(512);
            row2.setVgrow(Priority.NEVER);

            RowConstraints row3 = new RowConstraints();
            row3.setFillHeight(true);
            row3.setVgrow(Priority.ALWAYS);

            getRowConstraints().addAll(row0, row1, row2, row3);
        }

        private void initByteArrayImages() {
            for(int i = 0; i < IMAGE_COUNT; i++) {
                ByteArrayImage bai = new ByteArrayImage(idxCount);
                bais.add(bai);

                Label label = new Label("Step: 2^" + (8 * i));
                label.setAlignment(Pos.CENTER);
                add(label, i, 1);

                ImageView imageView = new ImageView();
                imageViews.add(imageView);
                refreshImage(i);
                add(imageView, i, 2);

                label.setPrefSize(imageView.getImage().getWidth(), 20);
            }
        }

        private void refreshImages() {
            for(int i = 0; i < IMAGE_COUNT; i++) {
                refreshImage(i);
            }
        }

        private void refreshImage(int i) {
            long step = 1L << (8 * i);
            bais.get(i).enableXorTransformation(ckTrafo.isSelected());
            Image image = bais.get(i).createImage(startIndex, 256, step);
            imageViews.get(i).setImage(image);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        BigInteger startIndex = new BigInteger("2345678765432");
        BigInteger idxCount = new BigInteger("7654321234567");

        MainPane mainPane = new MainPane(startIndex, idxCount);
        stage.setScene(new Scene(mainPane));
        stage.show();
    }
}
