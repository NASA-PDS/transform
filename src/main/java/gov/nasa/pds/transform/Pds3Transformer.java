// Copyright 2006-2012, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.transform;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.logging.Logger;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;

/**
 * Class that converts PDS3 data products into a viewable image file.
 *
 * @author mcayanan
 *
 */
public class Pds3Transformer implements PdsTransformer {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      Pds3Transformer.class.getName());

  @Override
  public void transform(final File input, final File output,
      final String format)
  throws TransformException {
    ManualPathResolver resolver = new ManualPathResolver();
    DefaultLabelParser parser = new DefaultLabelParser(false, true, resolver);
    Label label = null;
    try {
      label = parser.parseLabel(input.toURI().toURL());
    } catch (LabelParserException lp) {
      throw new TransformException("Problem while parsing input label: "
          + MessageUtils.getProblemMessage(lp));
    } catch (Exception e) {
      throw new TransformException("Problem while parsing input label: "
          + e.getMessage());
    }
    if (label.getPointers().isEmpty()) {
      throw new TransformException("No pointers found in the input label.");
    }
    for (PointerStatement pointer : label.getPointers()) {
      if (pointer.getIdentifier().getId().endsWith("IMAGE") ) {
        File imageFile = null;
        if (!pointer.getFileRefs().isEmpty()) {
          FileReference fileRef = pointer.getFileRefs().get(0);
          imageFile = new File(input.getParent(), fileRef.getPath());
          if (!imageFile.exists()) {
            throw new TransformException("Image file does not exist: "
                + imageFile.toString());
          }
          convert(imageFile, output, format, input);
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Successfully transformed into a viewable image file: "
              + output.toString(), input));
          break;
        } else {
          throw new TransformException("No image file references found "
              + "in label.");
        }
      }
    }
  }

  private void convert(final File image, final File output,
      final String format, final File inputLabel) {
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming PDS3 image file: " + image.toString(), inputLabel));
    RenderedImage renderedImage = JAI.create("ImageRead", image);
    ParameterBlockJAI paramBlock = new ParameterBlockJAI("imagewrite");
    paramBlock.addSource(renderedImage);
    paramBlock.setParameter("output", output.toString());
    paramBlock.setParameter("format", format);
    if (format.equalsIgnoreCase("tiff")
        || format.equalsIgnoreCase("tif")) {
      Dimension tilesize = new Dimension(renderedImage.getWidth(), 8);
      paramBlock.setParameter("tilesize", tilesize);
    }
    JAI.create("imagewrite", paramBlock);
  }
}
