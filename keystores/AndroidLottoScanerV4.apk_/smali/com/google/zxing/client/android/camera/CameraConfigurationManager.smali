.class final Lcom/google/zxing/client/android/camera/CameraConfigurationManager;
.super Ljava/lang/Object;
.source "CameraConfigurationManager.java"


# static fields
.field private static final COMMA_PATTERN:Ljava/util/regex/Pattern;

.field private static final TAG:Ljava/lang/String;

.field private static final TEN_DESIRED_ZOOM:I = 0x1b


# instance fields
.field private cameraResolution:Landroid/graphics/Point;

.field private final context:Landroid/content/Context;

.field private previewFormat:I

.field private previewFormatString:Ljava/lang/String;

.field private screenResolution:Landroid/graphics/Point;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 32
    const-class v0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    .line 36
    const-string v0, ","

    invoke-static {v0}, Ljava/util/regex/Pattern;->compile(Ljava/lang/String;)Ljava/util/regex/Pattern;

    move-result-object v0

    sput-object v0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->COMMA_PATTERN:Ljava/util/regex/Pattern;

    return-void
.end method

.method constructor <init>(Landroid/content/Context;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 44
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 45
    iput-object p1, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->context:Landroid/content/Context;

    .line 46
    return-void
.end method

.method private static findBestMotZoomValue(Ljava/lang/CharSequence;I)I
    .locals 14
    .param p0, "stringValues"    # Ljava/lang/CharSequence;
    .param p1, "tenDesiredZoom"    # I

    .prologue
    .line 165
    const/4 v2, 0x0

    .line 166
    .local v2, "tenBestValue":I
    sget-object v6, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->COMMA_PATTERN:Ljava/util/regex/Pattern;

    invoke-virtual {v6, p0}, Ljava/util/regex/Pattern;->split(Ljava/lang/CharSequence;)[Ljava/lang/String;

    move-result-object v7

    array-length v8, v7

    const/4 v6, 0x0

    :goto_0
    if-lt v6, v8, :cond_0

    move p1, v2

    .line 179
    .end local p1    # "tenDesiredZoom":I
    :goto_1
    return p1

    .line 166
    .restart local p1    # "tenDesiredZoom":I
    :cond_0
    aget-object v1, v7, v6

    .line 167
    .local v1, "stringValue":Ljava/lang/String;
    invoke-virtual {v1}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v1

    .line 170
    :try_start_0
    invoke-static {v1}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-wide v4

    .line 174
    .local v4, "value":D
    const-wide/high16 v10, 0x4024000000000000L    # 10.0

    mul-double/2addr v10, v4

    double-to-int v3, v10

    .line 175
    .local v3, "tenValue":I
    int-to-double v10, p1

    sub-double/2addr v10, v4

    invoke-static {v10, v11}, Ljava/lang/Math;->abs(D)D

    move-result-wide v10

    sub-int v9, p1, v2

    invoke-static {v9}, Ljava/lang/Math;->abs(I)I

    move-result v9

    int-to-double v12, v9

    cmpg-double v9, v10, v12

    if-gez v9, :cond_1

    .line 176
    move v2, v3

    .line 166
    :cond_1
    add-int/lit8 v6, v6, 0x1

    goto :goto_0

    .line 171
    .end local v3    # "tenValue":I
    .end local v4    # "value":D
    :catch_0
    move-exception v0

    .line 172
    .local v0, "nfe":Ljava/lang/NumberFormatException;
    goto :goto_1
.end method

.method private static findBestPreviewSizeValue(Ljava/lang/CharSequence;Landroid/graphics/Point;)Landroid/graphics/Point;
    .locals 16
    .param p0, "previewSizeValueString"    # Ljava/lang/CharSequence;
    .param p1, "screenResolution"    # Landroid/graphics/Point;

    .prologue
    .line 123
    const/4 v1, 0x0

    .line 124
    .local v1, "bestX":I
    const/4 v2, 0x0

    .line 125
    .local v2, "bestY":I
    const v3, 0x7fffffff

    .line 126
    .local v3, "diff":I
    sget-object v10, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->COMMA_PATTERN:Ljava/util/regex/Pattern;

    move-object/from16 v0, p0

    invoke-virtual {v10, v0}, Ljava/util/regex/Pattern;->split(Ljava/lang/CharSequence;)[Ljava/lang/String;

    move-result-object v11

    array-length v12, v11

    const/4 v10, 0x0

    :goto_0
    if-lt v10, v12, :cond_0

    .line 158
    :goto_1
    if-lez v1, :cond_4

    if-lez v2, :cond_4

    .line 159
    new-instance v10, Landroid/graphics/Point;

    invoke-direct {v10, v1, v2}, Landroid/graphics/Point;-><init>(II)V

    .line 161
    :goto_2
    return-object v10

    .line 126
    :cond_0
    aget-object v9, v11, v10

    .line 128
    .local v9, "previewSize":Ljava/lang/String;
    invoke-virtual {v9}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v9

    .line 129
    const/16 v13, 0x78

    invoke-virtual {v9, v13}, Ljava/lang/String;->indexOf(I)I

    move-result v4

    .line 130
    .local v4, "dimPosition":I
    if-gez v4, :cond_2

    .line 131
    sget-object v13, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v14, Ljava/lang/StringBuilder;

    const-string v15, "Bad preview-size: "

    invoke-direct {v14, v15}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v14, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v14

    invoke-static {v13, v14}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 126
    :cond_1
    :goto_3
    add-int/lit8 v10, v10, 0x1

    goto :goto_0

    .line 138
    :cond_2
    const/4 v13, 0x0

    :try_start_0
    invoke-virtual {v9, v13, v4}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v13

    invoke-static {v13}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v6

    .line 139
    .local v6, "newX":I
    add-int/lit8 v13, v4, 0x1

    invoke-virtual {v9, v13}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v13

    invoke-static {v13}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0

    move-result v7

    .line 145
    .local v7, "newY":I
    move-object/from16 v0, p1

    iget v13, v0, Landroid/graphics/Point;->x:I

    sub-int v13, v6, v13

    invoke-static {v13}, Ljava/lang/Math;->abs(I)I

    move-result v13

    move-object/from16 v0, p1

    iget v14, v0, Landroid/graphics/Point;->y:I

    sub-int v14, v7, v14

    invoke-static {v14}, Ljava/lang/Math;->abs(I)I

    move-result v14

    add-int v5, v13, v14

    .line 146
    .local v5, "newDiff":I
    if-nez v5, :cond_3

    .line 147
    move v1, v6

    .line 148
    move v2, v7

    .line 149
    goto :goto_1

    .line 140
    .end local v5    # "newDiff":I
    .end local v6    # "newX":I
    .end local v7    # "newY":I
    :catch_0
    move-exception v8

    .line 141
    .local v8, "nfe":Ljava/lang/NumberFormatException;
    sget-object v13, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v14, Ljava/lang/StringBuilder;

    const-string v15, "Bad preview-size: "

    invoke-direct {v14, v15}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v14, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v14

    invoke-static {v13, v14}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_3

    .line 150
    .end local v8    # "nfe":Ljava/lang/NumberFormatException;
    .restart local v5    # "newDiff":I
    .restart local v6    # "newX":I
    .restart local v7    # "newY":I
    :cond_3
    if-ge v5, v3, :cond_1

    .line 151
    move v1, v6

    .line 152
    move v2, v7

    .line 153
    move v3, v5

    goto :goto_3

    .line 161
    .end local v4    # "dimPosition":I
    .end local v5    # "newDiff":I
    .end local v6    # "newX":I
    .end local v7    # "newY":I
    .end local v9    # "previewSize":Ljava/lang/String;
    :cond_4
    const/4 v10, 0x0

    goto :goto_2
.end method

.method private static getCameraResolution(Landroid/hardware/Camera$Parameters;Landroid/graphics/Point;)Landroid/graphics/Point;
    .locals 5
    .param p0, "parameters"    # Landroid/hardware/Camera$Parameters;
    .param p1, "screenResolution"    # Landroid/graphics/Point;

    .prologue
    .line 99
    const-string v2, "preview-size-values"

    invoke-virtual {p0, v2}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 101
    .local v1, "previewSizeValueString":Ljava/lang/String;
    if-nez v1, :cond_0

    .line 102
    const-string v2, "preview-size-value"

    invoke-virtual {p0, v2}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 105
    :cond_0
    const/4 v0, 0x0

    .line 107
    .local v0, "cameraResolution":Landroid/graphics/Point;
    if-eqz v1, :cond_1

    .line 108
    sget-object v2, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "preview-size-values parameter: "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 109
    invoke-static {v1, p1}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->findBestPreviewSizeValue(Ljava/lang/CharSequence;Landroid/graphics/Point;)Landroid/graphics/Point;

    move-result-object v0

    .line 112
    :cond_1
    if-nez v0, :cond_2

    .line 114
    new-instance v0, Landroid/graphics/Point;

    .line 115
    .end local v0    # "cameraResolution":Landroid/graphics/Point;
    iget v2, p1, Landroid/graphics/Point;->x:I

    shr-int/lit8 v2, v2, 0x3

    shl-int/lit8 v2, v2, 0x3

    .line 116
    iget v3, p1, Landroid/graphics/Point;->y:I

    shr-int/lit8 v3, v3, 0x3

    shl-int/lit8 v3, v3, 0x3

    .line 114
    invoke-direct {v0, v2, v3}, Landroid/graphics/Point;-><init>(II)V

    .line 119
    .restart local v0    # "cameraResolution":Landroid/graphics/Point;
    :cond_2
    return-object v0
.end method

.method private setFlash(Landroid/hardware/Camera$Parameters;)V
    .locals 2
    .param p1, "parameters"    # Landroid/hardware/Camera$Parameters;

    .prologue
    .line 188
    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;

    const-string v1, "Behold II"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    sget v0, Lcom/google/zxing/client/android/camera/CameraManager;->SDK_INT:I

    const/4 v1, 0x3

    if-ne v0, v1, :cond_0

    .line 189
    const-string v0, "flash-value"

    const/4 v1, 0x1

    invoke-virtual {p1, v0, v1}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;I)V

    .line 194
    :goto_0
    const-string v0, "flash-mode"

    const-string v1, "off"

    invoke-virtual {p1, v0, v1}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 195
    return-void

    .line 191
    :cond_0
    const-string v0, "flash-value"

    const/4 v1, 0x2

    invoke-virtual {p1, v0, v1}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;I)V

    goto :goto_0
.end method

.method private setZoom(Landroid/hardware/Camera$Parameters;)V
    .locals 18
    .param p1, "parameters"    # Landroid/hardware/Camera$Parameters;

    .prologue
    .line 199
    const-string v13, "zoom-supported"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    .line 200
    .local v12, "zoomSupportedString":Ljava/lang/String;
    if-eqz v12, :cond_1

    invoke-static {v12}, Ljava/lang/Boolean;->parseBoolean(Ljava/lang/String;)Z

    move-result v13

    if-nez v13, :cond_1

    .line 259
    :cond_0
    :goto_0
    return-void

    .line 204
    :cond_1
    const/16 v9, 0x1b

    .line 206
    .local v9, "tenDesiredZoom":I
    const-string v13, "max-zoom"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 207
    .local v2, "maxZoomString":Ljava/lang/String;
    if-eqz v2, :cond_2

    .line 209
    const-wide/high16 v14, 0x4024000000000000L    # 10.0

    :try_start_0
    invoke-static {v2}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-wide v16

    mul-double v14, v14, v16

    double-to-int v10, v14

    .line 210
    .local v10, "tenMaxZoom":I
    if-le v9, v10, :cond_2

    .line 211
    move v9, v10

    .line 218
    .end local v10    # "tenMaxZoom":I
    :cond_2
    :goto_1
    const-string v13, "taking-picture-zoom-max"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    .line 219
    .local v8, "takingPictureZoomMaxString":Ljava/lang/String;
    if-eqz v8, :cond_3

    .line 221
    :try_start_1
    invoke-static {v8}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_1
    .catch Ljava/lang/NumberFormatException; {:try_start_1 .. :try_end_1} :catch_1

    move-result v10

    .line 222
    .restart local v10    # "tenMaxZoom":I
    if-le v9, v10, :cond_3

    .line 223
    move v9, v10

    .line 230
    .end local v10    # "tenMaxZoom":I
    :cond_3
    :goto_2
    const-string v13, "mot-zoom-values"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    .line 231
    .local v6, "motZoomValuesString":Ljava/lang/String;
    if-eqz v6, :cond_4

    .line 232
    invoke-static {v6, v9}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->findBestMotZoomValue(Ljava/lang/CharSequence;I)I

    move-result v9

    .line 235
    :cond_4
    const-string v13, "mot-zoom-step"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 236
    .local v3, "motZoomStepString":Ljava/lang/String;
    if-eqz v3, :cond_5

    .line 238
    :try_start_2
    invoke-virtual {v3}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v13

    invoke-static {v13}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v4

    .line 239
    .local v4, "motZoomStep":D
    const-wide/high16 v14, 0x4024000000000000L    # 10.0

    mul-double/2addr v14, v4

    double-to-int v11, v14

    .line 240
    .local v11, "tenZoomStep":I
    const/4 v13, 0x1

    if-le v11, v13, :cond_5

    .line 241
    rem-int v13, v9, v11
    :try_end_2
    .catch Ljava/lang/NumberFormatException; {:try_start_2 .. :try_end_2} :catch_2

    sub-int/2addr v9, v13

    .line 250
    .end local v4    # "motZoomStep":D
    .end local v11    # "tenZoomStep":I
    :cond_5
    :goto_3
    if-nez v2, :cond_6

    if-eqz v6, :cond_7

    .line 251
    :cond_6
    const-string v13, "zoom"

    int-to-double v14, v9

    const-wide/high16 v16, 0x4024000000000000L    # 10.0

    div-double v14, v14, v16

    invoke-static {v14, v15}, Ljava/lang/String;->valueOf(D)Ljava/lang/String;

    move-result-object v14

    move-object/from16 v0, p1

    invoke-virtual {v0, v13, v14}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 256
    :cond_7
    if-eqz v8, :cond_0

    .line 257
    const-string v13, "taking-picture-zoom"

    move-object/from16 v0, p1

    invoke-virtual {v0, v13, v9}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;I)V

    goto :goto_0

    .line 213
    .end local v3    # "motZoomStepString":Ljava/lang/String;
    .end local v6    # "motZoomValuesString":Ljava/lang/String;
    .end local v8    # "takingPictureZoomMaxString":Ljava/lang/String;
    :catch_0
    move-exception v7

    .line 214
    .local v7, "nfe":Ljava/lang/NumberFormatException;
    sget-object v13, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v14, Ljava/lang/StringBuilder;

    const-string v15, "Bad max-zoom: "

    invoke-direct {v14, v15}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v14, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v14

    invoke-static {v13, v14}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_1

    .line 225
    .end local v7    # "nfe":Ljava/lang/NumberFormatException;
    .restart local v8    # "takingPictureZoomMaxString":Ljava/lang/String;
    :catch_1
    move-exception v7

    .line 226
    .restart local v7    # "nfe":Ljava/lang/NumberFormatException;
    sget-object v13, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v14, Ljava/lang/StringBuilder;

    const-string v15, "Bad taking-picture-zoom-max: "

    invoke-direct {v14, v15}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v14, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v14

    invoke-static {v13, v14}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_2

    .line 243
    .end local v7    # "nfe":Ljava/lang/NumberFormatException;
    .restart local v3    # "motZoomStepString":Ljava/lang/String;
    .restart local v6    # "motZoomValuesString":Ljava/lang/String;
    :catch_2
    move-exception v13

    goto :goto_3
.end method


# virtual methods
.method getCameraResolution()Landroid/graphics/Point;
    .locals 1

    .prologue
    .line 82
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    return-object v0
.end method

.method getPreviewFormat()I
    .locals 1

    .prologue
    .line 90
    iget v0, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormat:I

    return v0
.end method

.method getPreviewFormatString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 94
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormatString:Ljava/lang/String;

    return-object v0
.end method

.method getScreenResolution()Landroid/graphics/Point;
    .locals 1

    .prologue
    .line 86
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->screenResolution:Landroid/graphics/Point;

    return-object v0
.end method

.method initFromCameraParameters(Landroid/hardware/Camera;Lcom/google/zxing/client/android/ViewfinderView;)V
    .locals 5
    .param p1, "camera"    # Landroid/hardware/Camera;
    .param p2, "viewfinderView"    # Lcom/google/zxing/client/android/ViewfinderView;

    .prologue
    .line 53
    invoke-virtual {p1}, Landroid/hardware/Camera;->getParameters()Landroid/hardware/Camera$Parameters;

    move-result-object v1

    .line 54
    .local v1, "parameters":Landroid/hardware/Camera$Parameters;
    invoke-virtual {v1}, Landroid/hardware/Camera$Parameters;->getPreviewFormat()I

    move-result v2

    iput v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormat:I

    .line 55
    const-string v2, "preview-format"

    invoke-virtual {v1, v2}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormatString:Ljava/lang/String;

    .line 56
    sget-object v2, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "Default preview format: "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget v4, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormat:I

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const/16 v4, 0x2f

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v3

    iget-object v4, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->previewFormatString:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 57
    iget-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->context:Landroid/content/Context;

    const-string v3, "window"

    invoke-virtual {v2, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/view/WindowManager;

    .line 59
    .local v0, "manager":Landroid/view/WindowManager;
    new-instance v2, Landroid/graphics/Point;

    invoke-virtual {p2}, Lcom/google/zxing/client/android/ViewfinderView;->getWidth()I

    move-result v3

    invoke-virtual {p2}, Lcom/google/zxing/client/android/ViewfinderView;->getHeight()I

    move-result v4

    invoke-direct {v2, v3, v4}, Landroid/graphics/Point;-><init>(II)V

    iput-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->screenResolution:Landroid/graphics/Point;

    .line 60
    sget-object v2, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "Screen resolution: "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v4, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->screenResolution:Landroid/graphics/Point;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 61
    iget-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->screenResolution:Landroid/graphics/Point;

    invoke-static {v1, v2}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getCameraResolution(Landroid/hardware/Camera$Parameters;Landroid/graphics/Point;)Landroid/graphics/Point;

    move-result-object v2

    iput-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    .line 62
    sget-object v2, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "Camera resolution: "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v4, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 63
    return-void
.end method

.method setDesiredCameraParameters(Landroid/hardware/Camera;)V
    .locals 4
    .param p1, "camera"    # Landroid/hardware/Camera;

    .prologue
    .line 72
    invoke-virtual {p1}, Landroid/hardware/Camera;->getParameters()Landroid/hardware/Camera$Parameters;

    move-result-object v0

    .line 73
    .local v0, "parameters":Landroid/hardware/Camera$Parameters;
    sget-object v1, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "Setting preview size: "

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v3, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 74
    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    iget v1, v1, Landroid/graphics/Point;->x:I

    iget-object v2, p0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->cameraResolution:Landroid/graphics/Point;

    iget v2, v2, Landroid/graphics/Point;->y:I

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->setPreviewSize(II)V

    .line 75
    invoke-direct {p0, v0}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->setFlash(Landroid/hardware/Camera$Parameters;)V

    .line 76
    invoke-direct {p0, v0}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->setZoom(Landroid/hardware/Camera$Parameters;)V

    .line 78
    invoke-virtual {p1, v0}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 79
    return-void
.end method
