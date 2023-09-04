package tictim.paraglider.mixin;

import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tictim.paraglider.client.util.Matrix4fAccess;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements Matrix4fAccess {
    @Shadow protected float m00;
    @Shadow protected float m01;
    @Shadow protected float m02;
    @Shadow protected float m03;
    @Shadow protected float m10;
    @Shadow protected float m11;
    @Shadow protected float m12;
    @Shadow protected float m13;
    @Shadow protected float m20;
    @Shadow protected float m21;
    @Shadow protected float m22;
    @Shadow protected float m23;
    @Shadow protected float m30;
    @Shadow protected float m31;
    @Shadow
    protected float m32;
    @Shadow protected float m33;

    @Override
    public void copyFromArray(float[] m) {
        if (m.length != 16) return;
        this.m00 = m[0];
        this.m10 = m[1];
        this.m20 = m[2];
        this.m30 = m[3];
        this.m01 = m[4];
        this.m11 = m[5];
        this.m21 = m[6];
        this.m31 = m[7];
        this.m02 = m[8];
        this.m12 = m[9];
        this.m22 = m[10];
        this.m32 = m[11];
        this.m03 = m[12];
        this.m13 = m[13];
        this.m23 = m[14];
        this.m33 = m[15];
    }
}
