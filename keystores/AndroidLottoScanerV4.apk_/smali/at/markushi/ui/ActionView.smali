.class public Lat/markushi/ui/ActionView;
.super Landroid/view/View;
.source "ActionView.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lat/markushi/ui/ActionView$SavedState;
    }
.end annotation


# static fields
.field public static final ROTATE_CLOCKWISE:I = 0x0

.field public static final ROTATE_COUNTER_CLOCKWISE:I = 0x1

.field private static final STROKE_SIZE_DIP:I = 0x2


# instance fields
.field private animateWhenReady:Z

.field private animatedAction:Lat/markushi/ui/action/Action;

.field private animationDuration:J

.field private animationProgress:F

.field private centerX:F

.field private centerY:F

.field private color:I

.field private currentAction:Lat/markushi/ui/action/Action;

.field private oldAction:Lat/markushi/ui/action/Action;

.field private padding:I

.field private paint:Landroid/graphics/Paint;

.field private path:Landroid/graphics/Path;

.field private ready:Z

.field private rotation:I

.field private scale:F

.field private size:I


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 72
    const/4 v0, 0x0

    invoke-direct {p0, p1, v0}, Lat/markushi/ui/ActionView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    .line 73
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    .prologue
    .line 76
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, v0}, Lat/markushi/ui/ActionView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 77
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 10
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    .prologue
    const/4 v9, 0x1

    const/4 v8, 0x0

    .line 80
    invoke-direct {p0, p1, p2, p3}, Landroid/view/View;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 65
    iput-boolean v8, p0, Lat/markushi/ui/ActionView;->ready:Z

    .line 66
    iput-boolean v8, p0, Lat/markushi/ui/ActionView;->animateWhenReady:Z

    .line 67
    iput v8, p0, Lat/markushi/ui/ActionView;->rotation:I

    .line 81
    invoke-virtual {p0}, Lat/markushi/ui/ActionView;->getResources()Landroid/content/res/Resources;

    move-result-object v5

    sget v6, Lit/neokree/materialtabs/R$integer;->av_animationDuration:I

    invoke-virtual {v5, v6}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v5

    int-to-long v6, v5

    iput-wide v6, p0, Lat/markushi/ui/ActionView;->animationDuration:J

    .line 82
    new-instance v5, Lat/markushi/ui/action/Action;

    const/16 v6, 0xc

    new-array v6, v6, [F

    const/4 v7, 0x0

    invoke-direct {v5, v6, v7}, Lat/markushi/ui/action/Action;-><init>([FLjava/util/List;)V

    iput-object v5, p0, Lat/markushi/ui/ActionView;->animatedAction:Lat/markushi/ui/action/Action;

    .line 84
    const/high16 v5, 0x40000000    # 2.0f

    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v6

    invoke-virtual {v6}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v6

    invoke-static {v9, v5, v6}, Landroid/util/TypedValue;->applyDimension(IFLandroid/util/DisplayMetrics;)F

    move-result v4

    .line 85
    .local v4, "strokeSize":F
    new-instance v5, Landroid/graphics/Path;

    invoke-direct {v5}, Landroid/graphics/Path;-><init>()V

    iput-object v5, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    .line 86
    new-instance v5, Landroid/graphics/Paint;

    invoke-direct {v5, v9}, Landroid/graphics/Paint;-><init>(I)V

    iput-object v5, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    .line 87
    iget-object v5, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    const v6, -0x22000001

    invoke-virtual {v5, v6}, Landroid/graphics/Paint;->setColor(I)V

    .line 88
    iget-object v5, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    invoke-virtual {v5, v4}, Landroid/graphics/Paint;->setStrokeWidth(F)V

    .line 89
    iget-object v5, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    sget-object v6, Landroid/graphics/Paint$Style;->STROKE:Landroid/graphics/Paint$Style;

    invoke-virtual {v5, v6}, Landroid/graphics/Paint;->setStyle(Landroid/graphics/Paint$Style;)V

    .line 91
    if-nez p2, :cond_0

    .line 102
    :goto_0
    return-void

    .line 94
    :cond_0
    sget-object v5, Lit/neokree/materialtabs/R$styleable;->ActionView:[I

    invoke-virtual {p1, p2, v5}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    move-result-object v0

    .line 95
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v5, Lit/neokree/materialtabs/R$styleable;->ActionView_av_color:I

    const v6, 0xddfffff

    invoke-virtual {v0, v5, v6}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v3

    .line 96
    .local v3, "color":I
    sget v5, Lit/neokree/materialtabs/R$styleable;->ActionView_av_action:I

    invoke-virtual {v0, v5, v8}, Landroid/content/res/TypedArray;->getInt(II)I

    move-result v2

    .line 97
    .local v2, "actionId":I
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    .line 99
    iget-object v5, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    invoke-virtual {v5, v3}, Landroid/graphics/Paint;->setColor(I)V

    .line 100
    invoke-direct {p0, v2}, Lat/markushi/ui/ActionView;->getActionFromEnum(I)Lat/markushi/ui/action/Action;

    move-result-object v1

    .line 101
    .local v1, "action":Lat/markushi/ui/action/Action;
    invoke-virtual {p0, v1}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;)V

    goto :goto_0
.end method

.method static synthetic access$0(Lat/markushi/ui/ActionView;Lat/markushi/ui/action/Action;ZI)V
    .locals 0

    .prologue
    .line 255
    invoke-direct {p0, p1, p2, p3}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;ZI)V

    return-void
.end method

.method private getActionFromEnum(I)Lat/markushi/ui/action/Action;
    .locals 1
    .param p1, "id"    # I

    .prologue
    .line 334
    packed-switch p1, :pswitch_data_0

    .line 345
    const/4 v0, 0x0

    :goto_0
    return-object v0

    .line 336
    :pswitch_0
    new-instance v0, Lat/markushi/ui/action/DrawerAction;

    invoke-direct {v0}, Lat/markushi/ui/action/DrawerAction;-><init>()V

    goto :goto_0

    .line 338
    :pswitch_1
    new-instance v0, Lat/markushi/ui/action/BackAction;

    invoke-direct {v0}, Lat/markushi/ui/action/BackAction;-><init>()V

    goto :goto_0

    .line 340
    :pswitch_2
    new-instance v0, Lat/markushi/ui/action/CloseAction;

    invoke-direct {v0}, Lat/markushi/ui/action/CloseAction;-><init>()V

    goto :goto_0

    .line 342
    :pswitch_3
    new-instance v0, Lat/markushi/ui/action/PlusAction;

    invoke-direct {v0}, Lat/markushi/ui/action/PlusAction;-><init>()V

    goto :goto_0

    .line 334
    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method private setAction(Lat/markushi/ui/action/Action;ZI)V
    .locals 3
    .param p1, "action"    # Lat/markushi/ui/action/Action;
    .param p2, "animate"    # Z
    .param p3, "rotation"    # I

    .prologue
    const/high16 v2, 0x3f800000    # 1.0f

    .line 256
    if-nez p1, :cond_1

    .line 287
    :cond_0
    :goto_0
    return-void

    .line 260
    :cond_1
    iput p3, p0, Lat/markushi/ui/ActionView;->rotation:I

    .line 261
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    if-nez v0, :cond_2

    .line 262
    iput-object p1, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    .line 263
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v0}, Lat/markushi/ui/action/Action;->flipHorizontally()V

    .line 264
    iput v2, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    .line 265
    invoke-static {p0}, Lat/markushi/ui/util/UiHelper;->postInvalidateOnAnimation(Landroid/view/View;)V

    goto :goto_0

    .line 269
    :cond_2
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v0

    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    .line 273
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    iput-object v0, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    .line 274
    iput-object p1, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    .line 276
    if-eqz p2, :cond_4

    .line 277
    const/4 v0, 0x0

    iput v0, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    .line 278
    iget-boolean v0, p0, Lat/markushi/ui/ActionView;->ready:Z

    if-eqz v0, :cond_3

    .line 279
    invoke-direct {p0}, Lat/markushi/ui/ActionView;->startAnimation()V

    goto :goto_0

    .line 281
    :cond_3
    const/4 v0, 0x1

    iput-boolean v0, p0, Lat/markushi/ui/ActionView;->animateWhenReady:Z

    goto :goto_0

    .line 284
    :cond_4
    iput v2, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    .line 285
    invoke-static {p0}, Lat/markushi/ui/util/UiHelper;->postInvalidateOnAnimation(Landroid/view/View;)V

    goto :goto_0
.end method

.method private startAnimation()V
    .locals 4

    .prologue
    .line 322
    iget-object v1, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v1}, Lat/markushi/ui/action/Action;->flipHorizontally()V

    .line 323
    iget-object v1, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v1}, Lat/markushi/ui/action/Action;->flipHorizontally()V

    .line 325
    invoke-direct {p0}, Lat/markushi/ui/ActionView;->transformActions()V

    .line 327
    iget-object v1, p0, Lat/markushi/ui/ActionView;->animatedAction:Lat/markushi/ui/action/Action;

    iget-object v2, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v2}, Lat/markushi/ui/action/Action;->getLineSegments()Ljava/util/List;

    move-result-object v2

    invoke-virtual {v1, v2}, Lat/markushi/ui/action/Action;->setLineSegments(Ljava/util/List;)V

    .line 328
    const-string v1, "animationProgress"

    const/4 v2, 0x2

    new-array v2, v2, [F

    fill-array-data v2, :array_0

    invoke-static {p0, v1, v2}, Landroid/animation/ObjectAnimator;->ofFloat(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;

    move-result-object v0

    .line 329
    .local v0, "animator":Landroid/animation/ObjectAnimator;
    invoke-static {}, Lat/markushi/ui/util/BakedBezierInterpolator;->getInstance()Lat/markushi/ui/util/BakedBezierInterpolator;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/animation/ObjectAnimator;->setInterpolator(Landroid/animation/TimeInterpolator;)V

    .line 330
    iget-wide v2, p0, Lat/markushi/ui/ActionView;->animationDuration:J

    invoke-virtual {v0, v2, v3}, Landroid/animation/ObjectAnimator;->setDuration(J)Landroid/animation/ObjectAnimator;

    move-result-object v1

    invoke-virtual {v1}, Landroid/animation/ObjectAnimator;->start()V

    .line 331
    return-void

    .line 328
    nop

    :array_0
    .array-data 4
        0x0
        0x3f800000    # 1.0f
    .end array-data
.end method

.method private transformActions()V
    .locals 5

    .prologue
    .line 312
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v0}, Lat/markushi/ui/action/Action;->isTransformed()Z

    move-result v0

    if-nez v0, :cond_0

    .line 313
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    iget v1, p0, Lat/markushi/ui/ActionView;->padding:I

    int-to-float v1, v1

    iget v2, p0, Lat/markushi/ui/ActionView;->padding:I

    int-to-float v2, v2

    iget v3, p0, Lat/markushi/ui/ActionView;->scale:F

    iget v4, p0, Lat/markushi/ui/ActionView;->size:I

    int-to-float v4, v4

    invoke-virtual {v0, v1, v2, v3, v4}, Lat/markushi/ui/action/Action;->transform(FFFF)V

    .line 316
    :cond_0
    iget-object v0, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v0}, Lat/markushi/ui/action/Action;->isTransformed()Z

    move-result v0

    if-nez v0, :cond_1

    .line 317
    iget-object v0, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    iget v1, p0, Lat/markushi/ui/ActionView;->padding:I

    int-to-float v1, v1

    iget v2, p0, Lat/markushi/ui/ActionView;->padding:I

    int-to-float v2, v2

    iget v3, p0, Lat/markushi/ui/ActionView;->scale:F

    iget v4, p0, Lat/markushi/ui/ActionView;->size:I

    int-to-float v4, v4

    invoke-virtual {v0, v1, v2, v3, v4}, Lat/markushi/ui/action/Action;->transform(FFFF)V

    .line 319
    :cond_1
    return-void
.end method

.method private updatePath(Lat/markushi/ui/action/Action;)V
    .locals 7
    .param p1, "action"    # Lat/markushi/ui/action/Action;

    .prologue
    .line 290
    iget-object v3, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    invoke-virtual {v3}, Landroid/graphics/Path;->reset()V

    .line 292
    invoke-virtual {p1}, Lat/markushi/ui/action/Action;->getLineData()[F

    move-result-object v0

    .line 294
    .local v0, "data":[F
    iget v3, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    const v4, 0x3f733333    # 0.95f

    cmpl-float v3, v3, v4

    if-lez v3, :cond_3

    invoke-virtual {p1}, Lat/markushi/ui/action/Action;->getLineSegments()Ljava/util/List;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/List;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_3

    .line 295
    invoke-virtual {p1}, Lat/markushi/ui/action/Action;->getLineSegments()Ljava/util/List;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-nez v4, :cond_2

    .line 309
    :cond_1
    return-void

    .line 295
    :cond_2
    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lat/markushi/ui/action/LineSegment;

    .line 296
    .local v2, "s":Lat/markushi/ui/action/LineSegment;
    iget-object v4, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    invoke-virtual {v2}, Lat/markushi/ui/action/LineSegment;->getStartIdx()I

    move-result v5

    add-int/lit8 v5, v5, 0x0

    aget v5, v0, v5

    invoke-virtual {v2}, Lat/markushi/ui/action/LineSegment;->getStartIdx()I

    move-result v6

    add-int/lit8 v6, v6, 0x1

    aget v6, v0, v6

    invoke-virtual {v4, v5, v6}, Landroid/graphics/Path;->moveTo(FF)V

    .line 297
    iget-object v4, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    invoke-virtual {v2}, Lat/markushi/ui/action/LineSegment;->getStartIdx()I

    move-result v5

    add-int/lit8 v5, v5, 0x2

    aget v5, v0, v5

    invoke-virtual {v2}, Lat/markushi/ui/action/LineSegment;->getStartIdx()I

    move-result v6

    add-int/lit8 v6, v6, 0x3

    aget v6, v0, v6

    invoke-virtual {v4, v5, v6}, Landroid/graphics/Path;->lineTo(FF)V

    .line 298
    const/4 v1, 0x1

    .local v1, "i":I
    :goto_0
    iget-object v4, v2, Lat/markushi/ui/action/LineSegment;->indexes:[I

    array-length v4, v4

    if-ge v1, v4, :cond_0

    .line 299
    iget-object v4, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    iget-object v5, v2, Lat/markushi/ui/action/LineSegment;->indexes:[I

    aget v5, v5, v1

    add-int/lit8 v5, v5, 0x0

    aget v5, v0, v5

    iget-object v6, v2, Lat/markushi/ui/action/LineSegment;->indexes:[I

    aget v6, v6, v1

    add-int/lit8 v6, v6, 0x1

    aget v6, v0, v6

    invoke-virtual {v4, v5, v6}, Landroid/graphics/Path;->lineTo(FF)V

    .line 300
    iget-object v4, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    iget-object v5, v2, Lat/markushi/ui/action/LineSegment;->indexes:[I

    aget v5, v5, v1

    add-int/lit8 v5, v5, 0x2

    aget v5, v0, v5

    iget-object v6, v2, Lat/markushi/ui/action/LineSegment;->indexes:[I

    aget v6, v6, v1

    add-int/lit8 v6, v6, 0x3

    aget v6, v0, v6

    invoke-virtual {v4, v5, v6}, Landroid/graphics/Path;->lineTo(FF)V

    .line 298
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 304
    .end local v1    # "i":I
    .end local v2    # "s":Lat/markushi/ui/action/LineSegment;
    :cond_3
    const/4 v1, 0x0

    .restart local v1    # "i":I
    :goto_1
    array-length v3, v0

    if-ge v1, v3, :cond_1

    .line 305
    iget-object v3, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    add-int/lit8 v4, v1, 0x0

    aget v4, v0, v4

    add-int/lit8 v5, v1, 0x1

    aget v5, v0, v5

    invoke-virtual {v3, v4, v5}, Landroid/graphics/Path;->moveTo(FF)V

    .line 306
    iget-object v3, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    add-int/lit8 v4, v1, 0x2

    aget v4, v0, v4

    add-int/lit8 v5, v1, 0x3

    aget v5, v0, v5

    invoke-virtual {v3, v4, v5}, Landroid/graphics/Path;->lineTo(FF)V

    .line 304
    add-int/lit8 v1, v1, 0x4

    goto :goto_1
.end method


# virtual methods
.method public getAction()Lat/markushi/ui/action/Action;
    .locals 1

    .prologue
    .line 172
    iget-object v0, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    return-object v0
.end method

.method public getAnimationProgress()F
    .locals 1

    .prologue
    .line 148
    iget v0, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    return v0
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 8
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    .prologue
    .line 123
    invoke-super {p0, p1}, Landroid/view/View;->onDraw(Landroid/graphics/Canvas;)V

    .line 125
    iget-object v5, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    if-nez v5, :cond_0

    .line 144
    :goto_0
    return-void

    .line 129
    :cond_0
    iget-object v5, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    if-nez v5, :cond_1

    .line 130
    iget-object v5, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-direct {p0, v5}, Lat/markushi/ui/ActionView;->updatePath(Lat/markushi/ui/action/Action;)V

    .line 142
    :goto_1
    iget v5, p0, Lat/markushi/ui/ActionView;->rotation:I

    if-nez v5, :cond_3

    const/high16 v5, 0x43340000    # 180.0f

    :goto_2
    iget v6, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    mul-float/2addr v5, v6

    iget v6, p0, Lat/markushi/ui/ActionView;->centerX:F

    iget v7, p0, Lat/markushi/ui/ActionView;->centerY:F

    invoke-virtual {p1, v5, v6, v7}, Landroid/graphics/Canvas;->rotate(FFF)V

    .line 143
    iget-object v5, p0, Lat/markushi/ui/ActionView;->path:Landroid/graphics/Path;

    iget-object v6, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    invoke-virtual {p1, v5, v6}, Landroid/graphics/Canvas;->drawPath(Landroid/graphics/Path;Landroid/graphics/Paint;)V

    goto :goto_0

    .line 132
    :cond_1
    const/high16 v5, 0x3f800000    # 1.0f

    iget v6, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    sub-float v3, v5, v6

    .line 133
    .local v3, "inverseProgress":F
    iget-object v5, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v5}, Lat/markushi/ui/action/Action;->getLineData()[F

    move-result-object v1

    .line 134
    .local v1, "current":[F
    iget-object v5, p0, Lat/markushi/ui/ActionView;->oldAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v5}, Lat/markushi/ui/action/Action;->getLineData()[F

    move-result-object v4

    .line 135
    .local v4, "old":[F
    iget-object v5, p0, Lat/markushi/ui/ActionView;->animatedAction:Lat/markushi/ui/action/Action;

    invoke-virtual {v5}, Lat/markushi/ui/action/Action;->getLineData()[F

    move-result-object v0

    .line 136
    .local v0, "animated":[F
    const/4 v2, 0x0

    .local v2, "i":I
    :goto_3
    array-length v5, v0

    if-lt v2, v5, :cond_2

    .line 139
    iget-object v5, p0, Lat/markushi/ui/ActionView;->animatedAction:Lat/markushi/ui/action/Action;

    invoke-direct {p0, v5}, Lat/markushi/ui/ActionView;->updatePath(Lat/markushi/ui/action/Action;)V

    goto :goto_1

    .line 137
    :cond_2
    aget v5, v1, v2

    iget v6, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    mul-float/2addr v5, v6

    aget v6, v4, v2

    mul-float/2addr v6, v3

    add-float/2addr v5, v6

    aput v5, v0, v2

    .line 136
    add-int/lit8 v2, v2, 0x1

    goto :goto_3

    .line 142
    .end local v0    # "animated":[F
    .end local v1    # "current":[F
    .end local v2    # "i":I
    .end local v3    # "inverseProgress":F
    .end local v4    # "old":[F
    :cond_3
    const/high16 v5, -0x3ccc0000    # -180.0f

    goto :goto_2
.end method

.method protected onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 2
    .param p1, "state"    # Landroid/os/Parcelable;

    .prologue
    .line 248
    move-object v0, p1

    check-cast v0, Lat/markushi/ui/ActionView$SavedState;

    .line 249
    .local v0, "ss":Lat/markushi/ui/ActionView$SavedState;
    invoke-virtual {v0}, Lat/markushi/ui/ActionView$SavedState;->getSuperState()Landroid/os/Parcelable;

    move-result-object v1

    invoke-super {p0, v1}, Landroid/view/View;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    .line 250
    iget v1, v0, Lat/markushi/ui/ActionView$SavedState;->color:I

    iput v1, p0, Lat/markushi/ui/ActionView;->color:I

    .line 251
    iget-object v1, v0, Lat/markushi/ui/ActionView$SavedState;->currentAction:Lat/markushi/ui/action/Action;

    iput-object v1, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    .line 252
    const/high16 v1, 0x3f800000    # 1.0f

    iput v1, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    .line 253
    return-void
.end method

.method protected onSaveInstanceState()Landroid/os/Parcelable;
    .locals 3

    .prologue
    .line 239
    invoke-super {p0}, Landroid/view/View;->onSaveInstanceState()Landroid/os/Parcelable;

    move-result-object v1

    .line 240
    .local v1, "superState":Landroid/os/Parcelable;
    new-instance v0, Lat/markushi/ui/ActionView$SavedState;

    invoke-direct {v0, v1}, Lat/markushi/ui/ActionView$SavedState;-><init>(Landroid/os/Parcelable;)V

    .line 241
    .local v0, "ss":Lat/markushi/ui/ActionView$SavedState;
    iget-object v2, p0, Lat/markushi/ui/ActionView;->currentAction:Lat/markushi/ui/action/Action;

    iput-object v2, v0, Lat/markushi/ui/ActionView$SavedState;->currentAction:Lat/markushi/ui/action/Action;

    .line 242
    iget v2, p0, Lat/markushi/ui/ActionView;->color:I

    iput v2, v0, Lat/markushi/ui/ActionView$SavedState;->color:I

    .line 243
    return-object v0
.end method

.method protected onSizeChanged(IIII)V
    .locals 2
    .param p1, "w"    # I
    .param p2, "h"    # I
    .param p3, "oldw"    # I
    .param p4, "oldh"    # I

    .prologue
    .line 106
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/View;->onSizeChanged(IIII)V

    .line 107
    div-int/lit8 v0, p1, 0x2

    int-to-float v0, v0

    iput v0, p0, Lat/markushi/ui/ActionView;->centerX:F

    .line 108
    div-int/lit8 v0, p2, 0x2

    int-to-float v0, v0

    iput v0, p0, Lat/markushi/ui/ActionView;->centerY:F

    .line 109
    invoke-virtual {p0}, Lat/markushi/ui/ActionView;->getPaddingLeft()I

    move-result v0

    iput v0, p0, Lat/markushi/ui/ActionView;->padding:I

    .line 110
    invoke-static {p1, p2}, Ljava/lang/Math;->min(II)I

    move-result v0

    iput v0, p0, Lat/markushi/ui/ActionView;->size:I

    .line 111
    invoke-static {p1, p2}, Ljava/lang/Math;->min(II)I

    move-result v0

    iget v1, p0, Lat/markushi/ui/ActionView;->padding:I

    mul-int/lit8 v1, v1, 0x2

    sub-int/2addr v0, v1

    int-to-float v0, v0

    iput v0, p0, Lat/markushi/ui/ActionView;->scale:F

    .line 112
    const/4 v0, 0x1

    iput-boolean v0, p0, Lat/markushi/ui/ActionView;->ready:Z

    .line 113
    invoke-direct {p0}, Lat/markushi/ui/ActionView;->transformActions()V

    .line 115
    iget-boolean v0, p0, Lat/markushi/ui/ActionView;->animateWhenReady:Z

    if-eqz v0, :cond_0

    .line 116
    const/4 v0, 0x0

    iput-boolean v0, p0, Lat/markushi/ui/ActionView;->animateWhenReady:Z

    .line 117
    invoke-direct {p0}, Lat/markushi/ui/ActionView;->startAnimation()V

    .line 119
    :cond_0
    return-void
.end method

.method public setAction(Lat/markushi/ui/action/Action;)V
    .locals 2
    .param p1, "action"    # Lat/markushi/ui/action/Action;

    .prologue
    .line 184
    const/4 v0, 0x1

    const/4 v1, 0x0

    invoke-direct {p0, p1, v0, v1}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;ZI)V

    .line 185
    return-void
.end method

.method public setAction(Lat/markushi/ui/action/Action;I)V
    .locals 1
    .param p1, "action"    # Lat/markushi/ui/action/Action;
    .param p2, "rotation"    # I

    .prologue
    .line 194
    const/4 v0, 0x1

    invoke-direct {p0, p1, v0, p2}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;ZI)V

    .line 195
    return-void
.end method

.method public setAction(Lat/markushi/ui/action/Action;Lat/markushi/ui/action/Action;IJ)V
    .locals 2
    .param p1, "fromAction"    # Lat/markushi/ui/action/Action;
    .param p2, "toAction"    # Lat/markushi/ui/action/Action;
    .param p3, "rotation"    # I
    .param p4, "delay"    # J

    .prologue
    const/4 v0, 0x0

    .line 216
    invoke-direct {p0, p1, v0, v0}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;ZI)V

    .line 217
    new-instance v0, Lat/markushi/ui/ActionView$1;

    invoke-direct {v0, p0, p2, p3}, Lat/markushi/ui/ActionView$1;-><init>(Lat/markushi/ui/ActionView;Lat/markushi/ui/action/Action;I)V

    invoke-virtual {p0, v0, p4, p5}, Lat/markushi/ui/ActionView;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 226
    return-void
.end method

.method public setAction(Lat/markushi/ui/action/Action;Z)V
    .locals 1
    .param p1, "action"    # Lat/markushi/ui/action/Action;
    .param p2, "animate"    # Z

    .prologue
    .line 204
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, v0}, Lat/markushi/ui/ActionView;->setAction(Lat/markushi/ui/action/Action;ZI)V

    .line 205
    return-void
.end method

.method public setAnimationDuration(J)V
    .locals 1
    .param p1, "animationDuration"    # J

    .prologue
    .line 234
    iput-wide p1, p0, Lat/markushi/ui/ActionView;->animationDuration:J

    .line 235
    return-void
.end method

.method public setAnimationProgress(F)V
    .locals 0
    .param p1, "animationProgress"    # F

    .prologue
    .line 153
    iput p1, p0, Lat/markushi/ui/ActionView;->animationProgress:F

    .line 154
    invoke-static {p0}, Lat/markushi/ui/util/UiHelper;->postInvalidateOnAnimation(Landroid/view/View;)V

    .line 155
    return-void
.end method

.method public setColor(I)V
    .locals 1
    .param p1, "color"    # I

    .prologue
    .line 163
    iput p1, p0, Lat/markushi/ui/ActionView;->color:I

    .line 164
    iget-object v0, p0, Lat/markushi/ui/ActionView;->paint:Landroid/graphics/Paint;

    invoke-virtual {v0, p1}, Landroid/graphics/Paint;->setColor(I)V

    .line 165
    invoke-static {p0}, Lat/markushi/ui/util/UiHelper;->postInvalidateOnAnimation(Landroid/view/View;)V

    .line 166
    return-void
.end method
