.class public final Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;
.super Lcom/google/zxing/LuminanceSource;
.source "PlanarYUVLuminanceSource.java"


# instance fields
.field private dataHeight:I

.field private dataWidth:I

.field private final left:I

.field private final top:I

.field private yuvData:[B


# direct methods
.method public constructor <init>([BIIIIIIZ)V
    .locals 2
    .param p1, "_yuvData"    # [B
    .param p2, "dataWidth"    # I
    .param p3, "dataHeight"    # I
    .param p4, "left"    # I
    .param p5, "top"    # I
    .param p6, "width"    # I
    .param p7, "height"    # I
    .param p8, "reverseHorizontal"    # Z

    .prologue
    .line 49
    invoke-direct {p0, p6, p7}, Lcom/google/zxing/LuminanceSource;-><init>(II)V

    .line 51
    add-int v0, p4, p6

    if-gt v0, p2, :cond_0

    add-int v0, p5, p7

    if-le v0, p3, :cond_1

    .line 52
    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "Crop rectangle does not fit within image data."

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 55
    :cond_1
    iput-object p1, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 56
    iput p2, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    .line 57
    iput p3, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    .line 58
    iput p4, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->left:I

    .line 59
    iput p5, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->top:I

    .line 61
    if-eqz p8, :cond_2

    .line 62
    invoke-direct {p0, p6, p7}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->reverseHorizontal(II)V

    .line 64
    :cond_2
    return-void
.end method

.method private reverseHorizontal(II)V
    .locals 9
    .param p1, "width"    # I
    .param p2, "height"    # I

    .prologue
    .line 196
    iget-object v6, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 198
    .local v6, "yuvData":[B
    const/4 v5, 0x0

    .local v5, "y":I
    iget v7, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->top:I

    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    mul-int/2addr v7, v8

    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->left:I

    add-int v1, v7, v8

    .local v1, "rowStart":I
    :goto_0
    if-lt v5, p2, :cond_0

    .line 207
    return-void

    .line 199
    :cond_0
    div-int/lit8 v7, p1, 0x2

    add-int v0, v1, v7

    .line 201
    .local v0, "middle":I
    move v3, v1

    .local v3, "x1":I
    add-int v7, v1, p1

    add-int/lit8 v4, v7, -0x1

    .local v4, "x2":I
    :goto_1
    if-lt v3, v0, :cond_1

    .line 198
    add-int/lit8 v5, v5, 0x1

    iget v7, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    add-int/2addr v1, v7

    goto :goto_0

    .line 202
    :cond_1
    aget-byte v2, v6, v3

    .line 203
    .local v2, "temp":B
    aget-byte v7, v6, v4

    aput-byte v7, v6, v3

    .line 204
    aput-byte v2, v6, v4

    .line 201
    add-int/lit8 v3, v3, 0x1

    add-int/lit8 v4, v4, -0x1

    goto :goto_1
.end method

.method private rotateYUV420Degree90([BII)[B
    .locals 6
    .param p1, "data"    # [B
    .param p2, "imageWidth"    # I
    .param p3, "imageHeight"    # I

    .prologue
    .line 112
    mul-int v4, p2, p3

    mul-int/lit8 v4, v4, 0x3

    div-int/lit8 v4, v4, 0x2

    new-array v3, v4, [B

    .line 114
    .local v3, "yuv":[B
    const/4 v0, 0x0

    .line 115
    .local v0, "i":I
    const/4 v1, 0x0

    .local v1, "x":I
    :goto_0
    if-lt v1, p2, :cond_0

    .line 122
    mul-int v4, p2, p3

    mul-int/lit8 v4, v4, 0x3

    div-int/lit8 v4, v4, 0x2

    add-int/lit8 v0, v4, -0x1

    .line 123
    add-int/lit8 v1, p2, -0x1

    :goto_1
    if-gtz v1, :cond_2

    .line 132
    return-object v3

    .line 116
    :cond_0
    add-int/lit8 v2, p3, -0x1

    .local v2, "y":I
    :goto_2
    if-gez v2, :cond_1

    .line 115
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 117
    :cond_1
    mul-int v4, v2, p2

    add-int/2addr v4, v1

    aget-byte v4, p1, v4

    aput-byte v4, v3, v0

    .line 118
    add-int/lit8 v0, v0, 0x1

    .line 116
    add-int/lit8 v2, v2, -0x1

    goto :goto_2

    .line 124
    .end local v2    # "y":I
    :cond_2
    const/4 v2, 0x0

    .restart local v2    # "y":I
    :goto_3
    div-int/lit8 v4, p3, 0x2

    if-lt v2, v4, :cond_3

    .line 123
    add-int/lit8 v1, v1, -0x2

    goto :goto_1

    .line 125
    :cond_3
    mul-int v4, p2, p3

    mul-int v5, v2, p2

    add-int/2addr v4, v5

    add-int/2addr v4, v1

    aget-byte v4, p1, v4

    aput-byte v4, v3, v0

    .line 126
    add-int/lit8 v0, v0, -0x1

    .line 127
    mul-int v4, p2, p3

    mul-int v5, v2, p2

    add-int/2addr v4, v5

    .line 128
    add-int/lit8 v5, v1, -0x1

    add-int/2addr v4, v5

    aget-byte v4, p1, v4

    .line 127
    aput-byte v4, v3, v0

    .line 129
    add-int/lit8 v0, v0, -0x1

    .line 124
    add-int/lit8 v2, v2, 0x1

    goto :goto_3
.end method


# virtual methods
.method public getDataHeight()I
    .locals 1

    .prologue
    .line 161
    iget v0, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    return v0
.end method

.method public getDataWidth()I
    .locals 1

    .prologue
    .line 157
    iget v0, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    return v0
.end method

.method public getMatrix()[B
    .locals 10

    .prologue
    .line 82
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getWidth()I

    move-result v5

    .line 83
    .local v5, "width":I
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getHeight()I

    move-result v1

    .line 87
    .local v1, "height":I
    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    if-ne v5, v8, :cond_1

    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    if-ne v1, v8, :cond_1

    .line 88
    iget-object v3, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 108
    :cond_0
    :goto_0
    return-object v3

    .line 91
    :cond_1
    mul-int v0, v5, v1

    .line 92
    .local v0, "area":I
    new-array v3, v0, [B

    .line 93
    .local v3, "matrix":[B
    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->top:I

    iget v9, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    mul-int/2addr v8, v9

    iget v9, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->left:I

    add-int v2, v8, v9

    .line 96
    .local v2, "inputOffset":I
    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    if-ne v5, v8, :cond_2

    .line 97
    iget-object v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    const/4 v9, 0x0

    invoke-static {v8, v2, v3, v9, v0}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    goto :goto_0

    .line 102
    :cond_2
    iget-object v7, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 103
    .local v7, "yuv":[B
    const/4 v6, 0x0

    .local v6, "y":I
    :goto_1
    if-ge v6, v1, :cond_0

    .line 104
    mul-int v4, v6, v5

    .line 105
    .local v4, "outputOffset":I
    invoke-static {v7, v2, v3, v4, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 106
    iget v8, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    add-int/2addr v2, v8

    .line 103
    add-int/lit8 v6, v6, 0x1

    goto :goto_1
.end method

.method public getRow(I[B)[B
    .locals 5
    .param p1, "y"    # I
    .param p2, "row"    # [B

    .prologue
    .line 68
    if-ltz p1, :cond_0

    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getHeight()I

    move-result v2

    if-lt p1, v2, :cond_1

    .line 69
    :cond_0
    new-instance v2, Ljava/lang/IllegalArgumentException;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "Requested row is outside the image: "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 71
    :cond_1
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getWidth()I

    move-result v1

    .line 72
    .local v1, "width":I
    if-eqz p2, :cond_2

    array-length v2, p2

    if-ge v2, v1, :cond_3

    .line 73
    :cond_2
    new-array p2, v1, [B

    .line 75
    :cond_3
    iget v2, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->top:I

    add-int/2addr v2, p1

    iget v3, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    mul-int/2addr v2, v3

    iget v3, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->left:I

    add-int v0, v2, v3

    .line 76
    .local v0, "offset":I
    iget-object v2, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    const/4 v3, 0x0

    invoke-static {v2, v0, p2, v3, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 77
    return-object p2
.end method

.method public isCropSupported()Z
    .locals 1

    .prologue
    .line 153
    const/4 v0, 0x1

    return v0
.end method

.method public isRotateSupported()Z
    .locals 1

    .prologue
    .line 148
    const/4 v0, 0x1

    return v0
.end method

.method public renderCroppedGreyscaleBitmap()Landroid/graphics/Bitmap;
    .locals 14

    .prologue
    const/4 v2, 0x0

    .line 165
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getWidth()I

    move-result v3

    .line 166
    .local v3, "width":I
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->getHeight()I

    move-result v7

    .line 167
    .local v7, "height":I
    mul-int v4, v3, v7

    new-array v1, v4, [I

    .line 168
    .local v1, "pixels":[I
    iget-object v13, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 169
    .local v13, "yuv":[B
    iget v4, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->top:I

    iget v5, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    mul-int/2addr v4, v5

    iget v5, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->left:I

    add-int v9, v4, v5

    .line 171
    .local v9, "inputOffset":I
    const/4 v12, 0x0

    .local v12, "y":I
    :goto_0
    if-lt v12, v7, :cond_0

    .line 183
    sget-object v4, Landroid/graphics/Bitmap$Config;->ARGB_8888:Landroid/graphics/Bitmap$Config;

    invoke-static {v3, v7, v4}, Landroid/graphics/Bitmap;->createBitmap(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;

    move-result-object v0

    .local v0, "bitmap":Landroid/graphics/Bitmap;
    move v4, v2

    move v5, v2

    move v6, v3

    .line 184
    invoke-virtual/range {v0 .. v7}, Landroid/graphics/Bitmap;->setPixels([IIIIIII)V

    .line 185
    return-object v0

    .line 172
    .end local v0    # "bitmap":Landroid/graphics/Bitmap;
    :cond_0
    mul-int v10, v12, v3

    .line 173
    .local v10, "outputOffset":I
    const/4 v11, 0x0

    .local v11, "x":I
    :goto_1
    if-lt v11, v3, :cond_1

    .line 177
    iget v4, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    add-int/2addr v9, v4

    .line 171
    add-int/lit8 v12, v12, 0x1

    goto :goto_0

    .line 174
    :cond_1
    add-int v4, v9, v11

    aget-byte v4, v13, v4

    and-int/lit16 v8, v4, 0xff

    .line 175
    .local v8, "grey":I
    add-int v4, v10, v11

    const/high16 v5, -0x1000000

    const v6, 0x10101

    mul-int/2addr v6, v8

    or-int/2addr v5, v6

    aput v5, v1, v4

    .line 173
    add-int/lit8 v11, v11, 0x1

    goto :goto_1
.end method

.method public rotateCounterClockwise()Lcom/google/zxing/LuminanceSource;
    .locals 4

    .prologue
    .line 138
    iget-object v1, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    iget v2, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    iget v3, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    invoke-direct {p0, v1, v2, v3}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->rotateYUV420Degree90([BII)[B

    move-result-object v1

    iput-object v1, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->yuvData:[B

    .line 139
    iget v0, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    .line 140
    .local v0, "tmp":I
    iget v1, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    iput v1, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataWidth:I

    .line 141
    iput v0, p0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;->dataHeight:I

    .line 143
    return-object p0
.end method
