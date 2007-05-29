package org.csstudio.trends.databrowser.model;

/** A wrapper around a ModelSample, morphing it to a custom info and type.
 *  <p>
 *  Used in ModelSamples to mark the last sample from the archive as such.
 *  @author Kay Kasemir
 */
public class ModelSampleMorpher extends ModelSample
{
    private final Type type;
    private final String info;
    
    ModelSampleMorpher(ModelSample sample, Type type, String info)
    {
        super(sample.getSample(), sample.getSource());
        this.type = type;
        this.info = info;
    }

    @Override
    public String getInfo()
    {
        final String orig = super.getInfo();
        if (orig != null)
            return info + ", " + orig; //$NON-NLS-1$
        return info;
    }

    @Override
    public Type getType()
    {   return type;    }
}
