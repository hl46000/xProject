.class public final Lcom/google/zxing/client/android/camera/CameraManager;
.super Ljava/lang/Object;
.source "CameraManager.java"


# static fields
.field static final SDK_INT:I

.field private static final TAG:Ljava/lang/String;

.field private static cameraManager:Lcom/google/zxing/client/android/camera/CameraManager;


# instance fields
.field private final autoFocusCallback:Lcom/google/zxing/client/android/camera/AutoFocusCallback;

.field private camera:Landroid/hardware/Camera;

.field private final configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

.field private final context:Landroid/content/Context;

.field private framingRect:Landroid/graphics/Rect;

.field private framingRectInPreview:Landroid/graphics/Rect;

.field private initialized:Z

.field private final previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

.field private previewing:Z

.field private final useOneShotPreviewCallback:Z


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .prologue
    .line 47
    const-class v2, Lcom/google/zxing/client/android/camera/CameraManager;

    invoke-virtual {v2}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v2

    sput-object v2, Lcom/google/zxing/client/android/camera/CameraManager;->TAG:Ljava/lang/String;

    .line 55
    :try_start_0
    sget-object v2, Landroid/os/Build$VERSION;->SDK:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0

    move-result v1

    .line 60
    .local v1, "sdkInt":I
    :goto_0
    sput v1, Lcom/google/zxing/client/android/camera/CameraManager;->SDK_INT:I

    .line 61
    return-void

    .line 56
    .end local v1    # "sdkInt":I
    :catch_0
    move-exception v0

    .line 58
    .local v0, "nfe":Ljava/lang/NumberFormatException;
    const/16 v1, 0x2710

    .restart local v1    # "sdkInt":I
    goto :goto_0
.end method

.method private constructor <init>(Landroid/content/Context;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 99
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 101
    iput-object p1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->context:Landroid/content/Context;

    .line 102
    new-instance v0, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-direct {v0, p1}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    .line 109
    sget-object v0, Landroid/os/Build$VERSION;->SDK:Ljava/lang/String;

    invoke-static {v0}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v0

    const/4 v1, 0x3

    if-le v0, v1, :cond_0

    const/4 v0, 0x1

    :goto_0
    iput-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->useOneShotPreviewCallback:Z

    .line 111
    new-instance v0, Lcom/google/zxing/client/android/camera/PreviewCallback;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    iget-boolean v2, p0, Lcom/google/zxing/client/android/camera/CameraManager;->useOneShotPreviewCallback:Z

    invoke-direct {v0, v1, v2}, Lcom/google/zxing/client/android/camera/PreviewCallback;-><init>(Lcom/google/zxing/client/android/camera/CameraConfigurationManager;Z)V

    iput-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

    .line 112
    new-instance v0, Lcom/google/zxing/client/android/camera/AutoFocusCallback;

    invoke-direct {v0}, Lcom/google/zxing/client/android/camera/AutoFocusCallback;-><init>()V

    iput-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->autoFocusCallback:Lcom/google/zxing/client/android/camera/AutoFocusCallback;

    .line 113
    return-void

    .line 109
    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public static get()Lcom/google/zxing/client/android/camera/CameraManager;
    .locals 1

    .prologue
    .line 96
    sget-object v0, Lcom/google/zxing/client/android/camera/CameraManager;->cameraManager:Lcom/google/zxing/client/android/camera/CameraManager;

    return-object v0
.end method

.method public static init(Landroid/content/Context;)V
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    .prologue
    .line 85
    sget-object v0, Lcom/google/zxing/client/android/camera/CameraManager;->cameraManager:Lcom/google/zxing/client/android/camera/CameraManager;

    if-nez v0, :cond_0

    .line 86
    new-instance v0, Lcom/google/zxing/client/android/camera/CameraManager;

    invoke-direct {v0, p0}, Lcom/google/zxing/client/android/camera/CameraManager;-><init>(Landroid/content/Context;)V

    sput-object v0, Lcom/google/zxing/client/android/camera/CameraManager;->cameraManager:Lcom/google/zxing/client/android/camera/CameraManager;

    .line 88
    :cond_0
    return-void
.end method


# virtual methods
.method public buildLuminanceSource([BIIZ)Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;
    .locals 12
    .param p1, "data"    # [B
    .param p2, "width"    # I
    .param p3, "height"    # I
    .param p4, "reverseHorizontal"    # Z

    .prologue
    .line 343
    invoke-virtual {p0}, Lcom/google/zxing/client/android/camera/CameraManager;->getFramingRectInPreview()Landroid/graphics/Rect;

    move-result-object v11

    .line 344
    .local v11, "rect":Landroid/graphics/Rect;
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v0}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getPreviewFormat()I

    move-result v9

    .line 345
    .local v9, "previewFormat":I
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v0}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getPreviewFormatString()Ljava/lang/String;

    move-result-object v10

    .line 349
    .local v10, "previewFormatString":Ljava/lang/String;
    packed-switch v9, :pswitch_data_0

    .line 367
    const-string v0, "yuv420p"

    invoke-virtual {v0, v10}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 368
    new-instance v0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;

    .line 371
    iget v4, v11, Landroid/graphics/Rect;->left:I

    .line 372
    iget v5, v11, Landroid/graphics/Rect;->top:I

    .line 373
    invoke-virtual {v11}, Landroid/graphics/Rect;->width()I

    move-result v6

    .line 374
    invoke-virtual {v11}, Landroid/graphics/Rect;->height()I

    move-result v7

    move-object v1, p1

    move v2, p2

    move v3, p3

    move/from16 v8, p4

    .line 368
    invoke-direct/range {v0 .. v8}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;-><init>([BIIIIIIZ)V

    :goto_0
    return-object v0

    .line 356
    :pswitch_0
    new-instance v0, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;

    .line 359
    iget v4, v11, Landroid/graphics/Rect;->left:I

    .line 360
    iget v5, v11, Landroid/graphics/Rect;->top:I

    .line 361
    invoke-virtual {v11}, Landroid/graphics/Rect;->width()I

    move-result v6

    .line 362
    invoke-virtual {v11}, Landroid/graphics/Rect;->height()I

    move-result v7

    move-object v1, p1

    move v2, p2

    move v3, p3

    move/from16 v8, p4

    .line 356
    invoke-direct/range {v0 .. v8}, Lcom/google/zxing/client/android/PlanarYUVLuminanceSource;-><init>([BIIIIIIZ)V

    goto :goto_0

    .line 378
    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "Unsupported picture format: "

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 379
    invoke-virtual {v1, v9}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const/16 v2, 0x2f

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 378
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 349
    :pswitch_data_0
    .packed-switch 0x10
        :pswitch_0
        :pswitch_0
    .end packed-switch
.end method

.method public closeDriver()V
    .locals 1

    .prologue
    .line 154
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-eqz v0, :cond_0

    .line 155
    invoke-static {}, Lcom/google/zxing/client/android/camera/FlashlightManager;->disableFlashlight()V

    .line 156
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0}, Landroid/hardware/Camera;->release()V

    .line 157
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    .line 159
    :cond_0
    return-void
.end method

.method public getFramingRect()Landroid/graphics/Rect;
    .locals 8

    .prologue
    .line 235
    iget-object v5, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v5}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getScreenResolution()Landroid/graphics/Point;

    move-result-object v2

    .line 237
    .local v2, "screenResolution":Landroid/graphics/Point;
    iget-object v5, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRect:Landroid/graphics/Rect;

    if-nez v5, :cond_1

    .line 238
    iget-object v5, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-nez v5, :cond_0

    const/4 v5, 0x0

    .line 260
    :goto_0
    return-object v5

    .line 241
    :cond_0
    iget v5, v2, Landroid/graphics/Point;->x:I

    mul-int/lit8 v5, v5, 0x3

    div-int/lit8 v5, v5, 0x5

    iget v6, v2, Landroid/graphics/Point;->y:I

    mul-int/lit8 v6, v6, 0x3

    div-int/lit8 v6, v6, 0x5

    invoke-static {v5, v6}, Ljava/lang/Math;->min(II)I

    move-result v4

    .line 242
    .local v4, "width":I
    move v0, v4

    .line 243
    .local v0, "height":I
    iget v5, v2, Landroid/graphics/Point;->x:I

    sub-int/2addr v5, v4

    div-int/lit8 v1, v5, 0x2

    .line 244
    .local v1, "leftOffset":I
    iget v5, v2, Landroid/graphics/Point;->y:I

    sub-int/2addr v5, v4

    div-int/lit8 v3, v5, 0x2

    .line 255
    .local v3, "topOffset":I
    new-instance v5, Landroid/graphics/Rect;

    add-int v6, v1, v4

    add-int v7, v3, v0

    invoke-direct {v5, v1, v3, v6, v7}, Landroid/graphics/Rect;-><init>(IIII)V

    iput-object v5, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRect:Landroid/graphics/Rect;

    .line 257
    sget-object v5, Lcom/google/zxing/client/android/camera/CameraManager;->TAG:Ljava/lang/String;

    new-instance v6, Ljava/lang/StringBuilder;

    const-string v7, "Calculated framing rect: "

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v7, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRect:Landroid/graphics/Rect;

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 260
    .end local v0    # "height":I
    .end local v1    # "leftOffset":I
    .end local v3    # "topOffset":I
    .end local v4    # "width":I
    :cond_1
    iget-object v5, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRect:Landroid/graphics/Rect;

    goto :goto_0
.end method

.method public getFramingRectInPreview()Landroid/graphics/Rect;
    .locals 5

    .prologue
    .line 293
    iget-object v3, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRectInPreview:Landroid/graphics/Rect;

    if-nez v3, :cond_0

    .line 294
    new-instance v1, Landroid/graphics/Rect;

    invoke-virtual {p0}, Lcom/google/zxing/client/android/camera/CameraManager;->getFramingRect()Landroid/graphics/Rect;

    move-result-object v3

    invoke-direct {v1, v3}, Landroid/graphics/Rect;-><init>(Landroid/graphics/Rect;)V

    .line 296
    .local v1, "rect":Landroid/graphics/Rect;
    iget-object v3, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v3}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getCameraResolution()Landroid/graphics/Point;

    move-result-object v0

    .line 297
    .local v0, "cameraResolution":Landroid/graphics/Point;
    iget-object v3, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    invoke-virtual {v3}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->getScreenResolution()Landroid/graphics/Point;

    move-result-object v2

    .line 299
    .local v2, "screenResolution":Landroid/graphics/Point;
    sget-object v3, Lcom/google/zxing/client/android/camera/CameraManager;->TAG:Ljava/lang/String;

    invoke-virtual {v0}, Landroid/graphics/Point;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 300
    sget-object v3, Lcom/google/zxing/client/android/camera/CameraManager;->TAG:Ljava/lang/String;

    invoke-virtual {v2}, Landroid/graphics/Point;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 301
    iget v3, v1, Landroid/graphics/Rect;->left:I

    iget v4, v0, Landroid/graphics/Point;->x:I

    mul-int/2addr v3, v4

    iget v4, v2, Landroid/graphics/Point;->x:I

    div-int/2addr v3, v4

    iput v3, v1, Landroid/graphics/Rect;->left:I

    .line 302
    iget v3, v1, Landroid/graphics/Rect;->right:I

    iget v4, v0, Landroid/graphics/Point;->x:I

    mul-int/2addr v3, v4

    iget v4, v2, Landroid/graphics/Point;->x:I

    div-int/2addr v3, v4

    iput v3, v1, Landroid/graphics/Rect;->right:I

    .line 303
    iget v3, v1, Landroid/graphics/Rect;->top:I

    iget v4, v0, Landroid/graphics/Point;->y:I

    mul-int/2addr v3, v4

    iget v4, v2, Landroid/graphics/Point;->y:I

    div-int/2addr v3, v4

    iput v3, v1, Landroid/graphics/Rect;->top:I

    .line 304
    iget v3, v1, Landroid/graphics/Rect;->bottom:I

    iget v4, v0, Landroid/graphics/Point;->y:I

    mul-int/2addr v3, v4

    iget v4, v2, Landroid/graphics/Point;->y:I

    div-int/2addr v3, v4

    iput v3, v1, Landroid/graphics/Rect;->bottom:I

    .line 306
    iput-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRectInPreview:Landroid/graphics/Rect;

    .line 309
    .end local v0    # "cameraResolution":Landroid/graphics/Point;
    .end local v1    # "rect":Landroid/graphics/Rect;
    .end local v2    # "screenResolution":Landroid/graphics/Point;
    :cond_0
    iget-object v3, p0, Lcom/google/zxing/client/android/camera/CameraManager;->framingRectInPreview:Landroid/graphics/Rect;

    return-object v3
.end method

.method public openDriver(Landroid/view/SurfaceHolder;Lcom/google/zxing/client/android/ViewfinderView;)V
    .locals 2
    .param p1, "holder"    # Landroid/view/SurfaceHolder;
    .param p2, "viewfinderView"    # Lcom/google/zxing/client/android/ViewfinderView;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 124
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-nez v0, :cond_0

    .line 125
    invoke-static {}, Landroid/hardware/Camera;->open()Landroid/hardware/Camera;

    move-result-object v0

    iput-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    .line 127
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-nez v0, :cond_0

    .line 128
    new-instance v0, Ljava/io/IOException;

    invoke-direct {v0}, Ljava/io/IOException;-><init>()V

    throw v0

    .line 132
    :cond_0
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    const/16 v1, 0x5a

    invoke-virtual {v0, v1}, Landroid/hardware/Camera;->setDisplayOrientation(I)V

    .line 133
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0, p1}, Landroid/hardware/Camera;->setPreviewDisplay(Landroid/view/SurfaceHolder;)V

    .line 135
    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->initialized:Z

    if-nez v0, :cond_1

    .line 136
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->initialized:Z

    .line 137
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0, v1, p2}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->initFromCameraParameters(Landroid/hardware/Camera;Lcom/google/zxing/client/android/ViewfinderView;)V

    .line 139
    :cond_1
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->configManager:Lcom/google/zxing/client/android/camera/CameraConfigurationManager;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0, v1}, Lcom/google/zxing/client/android/camera/CameraConfigurationManager;->setDesiredCameraParameters(Landroid/hardware/Camera;)V

    .line 140
    invoke-static {}, Lcom/google/zxing/client/android/camera/FlashlightManager;->enableFlashlight()V

    .line 148
    return-void
.end method

.method public requestAutoFocus(Landroid/os/Handler;I)V
    .locals 2
    .param p1, "handler"    # Landroid/os/Handler;
    .param p2, "message"    # I

    .prologue
    .line 220
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    if-eqz v0, :cond_0

    .line 221
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->autoFocusCallback:Lcom/google/zxing/client/android/camera/AutoFocusCallback;

    invoke-virtual {v0, p1, p2}, Lcom/google/zxing/client/android/camera/AutoFocusCallback;->setHandler(Landroid/os/Handler;I)V

    .line 223
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->autoFocusCallback:Lcom/google/zxing/client/android/camera/AutoFocusCallback;

    invoke-virtual {v0, v1}, Landroid/hardware/Camera;->autoFocus(Landroid/hardware/Camera$AutoFocusCallback;)V

    .line 225
    :cond_0
    return-void
.end method

.method public requestPreviewFrame(Landroid/os/Handler;I)V
    .locals 2
    .param p1, "handler"    # Landroid/os/Handler;
    .param p2, "message"    # I

    .prologue
    .line 203
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    if-eqz v0, :cond_0

    .line 204
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

    invoke-virtual {v0, p1, p2}, Lcom/google/zxing/client/android/camera/PreviewCallback;->setHandler(Landroid/os/Handler;I)V

    .line 205
    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->useOneShotPreviewCallback:Z

    if-eqz v0, :cond_1

    .line 206
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

    invoke-virtual {v0, v1}, Landroid/hardware/Camera;->setOneShotPreviewCallback(Landroid/hardware/Camera$PreviewCallback;)V

    .line 211
    :cond_0
    :goto_0
    return-void

    .line 208
    :cond_1
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

    invoke-virtual {v0, v1}, Landroid/hardware/Camera;->setPreviewCallback(Landroid/hardware/Camera$PreviewCallback;)V

    goto :goto_0
.end method

.method public setFlashMode(Z)V
    .locals 2
    .param p1, "bOnOff"    # Z

    .prologue
    .line 162
    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v1}, Landroid/hardware/Camera;->stopPreview()V

    .line 163
    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v1}, Landroid/hardware/Camera;->getParameters()Landroid/hardware/Camera$Parameters;

    move-result-object v0

    .line 164
    .local v0, "p":Landroid/hardware/Camera$Parameters;
    if-eqz p1, :cond_0

    const-string v1, "torch"

    :goto_0
    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setFlashMode(Ljava/lang/String;)V

    .line 165
    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v1, v0}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 166
    iget-object v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v1}, Landroid/hardware/Camera;->startPreview()V

    .line 167
    return-void

    .line 164
    :cond_0
    const-string v1, "off"

    goto :goto_0
.end method

.method public startPreview()V
    .locals 1

    .prologue
    .line 173
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    if-nez v0, :cond_0

    .line 174
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0}, Landroid/hardware/Camera;->startPreview()V

    .line 175
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    .line 177
    :cond_0
    return-void
.end method

.method public stopPreview()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    const/4 v1, 0x0

    .line 183
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    if-eqz v0, :cond_1

    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    if-eqz v0, :cond_1

    .line 184
    iget-boolean v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->useOneShotPreviewCallback:Z

    if-nez v0, :cond_0

    .line 185
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0, v2}, Landroid/hardware/Camera;->setPreviewCallback(Landroid/hardware/Camera$PreviewCallback;)V

    .line 187
    :cond_0
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->camera:Landroid/hardware/Camera;

    invoke-virtual {v0}, Landroid/hardware/Camera;->stopPreview()V

    .line 188
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewCallback:Lcom/google/zxing/client/android/camera/PreviewCallback;

    invoke-virtual {v0, v2, v1}, Lcom/google/zxing/client/android/camera/PreviewCallback;->setHandler(Landroid/os/Handler;I)V

    .line 189
    iget-object v0, p0, Lcom/google/zxing/client/android/camera/CameraManager;->autoFocusCallback:Lcom/google/zxing/client/android/camera/AutoFocusCallback;

    invoke-virtual {v0, v2, v1}, Lcom/google/zxing/client/android/camera/AutoFocusCallback;->setHandler(Landroid/os/Handler;I)V

    .line 190
    iput-boolean v1, p0, Lcom/google/zxing/client/android/camera/CameraManager;->previewing:Z

    .line 192
    :cond_1
    return-void
.end method
