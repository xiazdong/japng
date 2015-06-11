package net.ellerton.japng.util;

import net.ellerton.japng.PngChunkCode;
import net.ellerton.japng.reader.PngChunkProcessor;
import net.ellerton.japng.util.PngContainer;
import net.ellerton.japng.chunks.*;
import net.ellerton.japng.error.PngException;
import net.ellerton.japng.map.PngChunkMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * TODO: need to keep this?
 */
public abstract class PngContainerProcessor<ResultT> implements PngChunkProcessor<ResultT> {
    protected final PngContainer container;

    public PngContainerProcessor() {
        this.container = new PngContainer();
    }

    @Override
    public void processHeader(PngHeader header) throws PngException {
        container.header = header;
    }

    @Override
    public void processGamma(PngGamma pngGamma) {
        container.gamma = pngGamma;
    }


    @Override
    public void processPalette(byte[] bytes, int position, int length) throws PngException {
        container.palette = PngPalette.from(bytes, position, length);
    }


    @Override
    public void processTransparency(byte[] bytes, int position, int length) throws PngException {
        // NOP
    }

    @Override
    public void processAnimationControl(PngAnimationControl animationControl) {
        container.animationControl = animationControl;
        container.animationFrames = new ArrayList<PngFrameControl>(container.animationControl.numFrames);
    }

    @Override
    public void processFrameControl(PngFrameControl pngFrameControl) throws PngException {
        container.animationFrames.add(pngFrameControl);
        container.currentFrame = pngFrameControl;
//        imageDataProcessor = builder.makeFrameImageProcessor(container.currentFrame);
    }

    @Override
    public void processDefaultImageData(InputStream inputStream, PngChunkCode code, int position, int length) throws IOException, PngException {
        if (null != container.currentFrame) {
            throw new IllegalStateException("Attempt to process main frame image data but an animation frame is in place");
        }
        // TODO: keep this?
    }

    @Override
    public void processFrameImageData(InputStream inputStream, PngChunkCode code, int position, int length) throws IOException, PngException {
        if (null == container.currentFrame) {
            throw new IllegalStateException("Attempt to process animation frame image data without a frame in place");
        }
        container.currentFrame.appendImageData(new PngChunkMap(code, position, length, 0));

        // TODO: keep this?
    }

    @Override
    public void processChunkMapItem(PngChunkMap pngChunkMap) {
        container.chunks.add(pngChunkMap);
    }

    public PngContainer getContainer() {
        return container;
    }
}
