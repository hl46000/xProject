.class public Lit/neokree/materialtabs/MaterialTabHost;
.super Landroid/widget/RelativeLayout;
.source "MaterialTabHost.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation build Landroid/annotation/SuppressLint;
    value = {
        "InflateParams"
    }
.end annotation


# static fields
.field private static tabSelected:I


# instance fields
.field private accentColor:I

.field private density:F

.field private hasIcons:Z

.field private iconColor:I

.field private isTablet:Z

.field private layout:Landroid/widget/LinearLayout;

.field private left:Landroid/widget/ImageButton;

.field private primaryColor:I

.field private right:Landroid/widget/ImageButton;

.field private scrollView:Landroid/widget/HorizontalScrollView;

.field private scrollable:Z

.field private tabs:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Lit/neokree/materialtabs/MaterialTab;",
            ">;"
        }
    .end annotation
.end field

.field private textColor:I


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 45
    const/4 v0, 0x0

    invoke-direct {p0, p1, v0}, Lit/neokree/materialtabs/MaterialTabHost;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    .line 46
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    .prologue
    .line 49
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, v0}, Lit/neokree/materialtabs/MaterialTabHost;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 50
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    .prologue
    const/4 v3, 0x0

    .line 53
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/RelativeLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 55
    new-instance v1, Landroid/widget/HorizontalScrollView;

    invoke-direct {v1, p1}, Landroid/widget/HorizontalScrollView;-><init>(Landroid/content/Context;)V

    iput-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    .line 56
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    const/4 v2, 0x2

    invoke-virtual {v1, v2}, Landroid/widget/HorizontalScrollView;->setOverScrollMode(I)V

    .line 57
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    invoke-virtual {v1, v3}, Landroid/widget/HorizontalScrollView;->setHorizontalScrollBarEnabled(Z)V

    .line 58
    new-instance v1, Landroid/widget/LinearLayout;

    invoke-direct {v1, p1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    iput-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    .line 59
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    iget-object v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v1, v2}, Landroid/widget/HorizontalScrollView;->addView(Landroid/view/View;)V

    .line 62
    if-eqz p2, :cond_0

    .line 63
    invoke-virtual {p1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    move-result-object v1

    sget-object v2, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost:[I

    invoke-virtual {v1, p2, v2, v3, v3}, Landroid/content/res/Resources$Theme;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    move-result-object v0

    .line 67
    .local v0, "a":Landroid/content/res/TypedArray;
    :try_start_0
    sget v1, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost_hasIcons:I

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    move-result v1

    iput-boolean v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->hasIcons:Z

    .line 69
    sget v1, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost_materialTabsPrimaryColor:I

    const-string v2, "#009688"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v1

    iput v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->primaryColor:I

    .line 70
    sget v1, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost_accentColor:I

    const-string v2, "#00b0ff"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v1

    iput v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->accentColor:I

    .line 71
    sget v1, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost_iconColor:I

    const/4 v2, -0x1

    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v1

    iput v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->iconColor:I

    .line 72
    sget v1, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost_textColor:I

    const/4 v2, -0x1

    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v1

    iput v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->textColor:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 74
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    .line 81
    .end local v0    # "a":Landroid/content/res/TypedArray;
    :goto_0
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->isInEditMode()Z

    .line 82
    iput-boolean v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    .line 83
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget v2, Lit/neokree/materialtabs/R$bool;->isTablet:I

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getBoolean(I)Z

    move-result v1

    iput-boolean v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->isTablet:Z

    .line 84
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v1

    iget v1, v1, Landroid/util/DisplayMetrics;->density:F

    iput v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    .line 85
    sput v3, Lit/neokree/materialtabs/MaterialTabHost;->tabSelected:I

    .line 88
    new-instance v1, Ljava/util/LinkedList;

    invoke-direct {v1}, Ljava/util/LinkedList;-><init>()V

    iput-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    .line 91
    iget v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->primaryColor:I

    invoke-super {p0, v1}, Landroid/widget/RelativeLayout;->setBackgroundColor(I)V

    .line 92
    return-void

    .line 73
    .restart local v0    # "a":Landroid/content/res/TypedArray;
    :catchall_0
    move-exception v1

    .line 74
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    .line 75
    throw v1

    .line 78
    .end local v0    # "a":Landroid/content/res/TypedArray;
    :cond_0
    iput-boolean v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->hasIcons:Z

    goto :goto_0
.end method

.method private scrollTo(I)V
    .locals 6
    .param p1, "position"    # I

    .prologue
    .line 180
    const/4 v1, 0x0

    .line 181
    .local v1, "totalWidth":I
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    if-lt v0, p1, :cond_0

    .line 192
    iget-object v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    const/4 v4, 0x0

    invoke-virtual {v3, v1, v4}, Landroid/widget/HorizontalScrollView;->smoothScrollTo(II)V

    .line 193
    return-void

    .line 182
    :cond_0
    iget-object v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v3, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v3}, Lit/neokree/materialtabs/MaterialTab;->getView()Landroid/view/View;

    move-result-object v3

    invoke-virtual {v3}, Landroid/view/View;->getWidth()I

    move-result v2

    .line 183
    .local v2, "width":I
    if-nez v2, :cond_1

    .line 184
    iget-boolean v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->isTablet:Z

    if-nez v3, :cond_2

    .line 185
    iget-object v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v3, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v3}, Lit/neokree/materialtabs/MaterialTab;->getTabMinWidth()I

    move-result v3

    int-to-float v3, v3

    const/high16 v4, 0x41c00000    # 24.0f

    iget v5, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v4, v5

    add-float/2addr v3, v4

    float-to-int v2, v3

    .line 190
    :cond_1
    :goto_1
    add-int/2addr v1, v2

    .line 181
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 187
    :cond_2
    iget-object v3, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v3, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v3}, Lit/neokree/materialtabs/MaterialTab;->getTabMinWidth()I

    move-result v3

    int-to-float v3, v3

    const/high16 v4, 0x42400000    # 48.0f

    iget v5, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v4, v5

    add-float/2addr v3, v4

    float-to-int v2, v3

    goto :goto_1
.end method


# virtual methods
.method public addTab(Lit/neokree/materialtabs/MaterialTab;)V
    .locals 3
    .param p1, "tab"    # Lit/neokree/materialtabs/MaterialTab;

    .prologue
    const/4 v2, 0x1

    .line 130
    iget v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->accentColor:I

    invoke-virtual {p1, v0}, Lit/neokree/materialtabs/MaterialTab;->setAccentColor(I)V

    .line 131
    iget v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->primaryColor:I

    invoke-virtual {p1, v0}, Lit/neokree/materialtabs/MaterialTab;->setPrimaryColor(I)V

    .line 132
    iget v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->textColor:I

    invoke-virtual {p1, v0}, Lit/neokree/materialtabs/MaterialTab;->setTextColor(I)V

    .line 133
    iget v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->iconColor:I

    invoke-virtual {p1, v0}, Lit/neokree/materialtabs/MaterialTab;->setIconColor(I)V

    .line 134
    iget-object v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    invoke-virtual {p1, v0}, Lit/neokree/materialtabs/MaterialTab;->setPosition(I)V

    .line 137
    iget-object v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 139
    iget-object v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    const/4 v1, 0x4

    if-ne v0, v1, :cond_0

    iget-boolean v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->hasIcons:Z

    if-nez v0, :cond_0

    .line 141
    iput-boolean v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    .line 144
    :cond_0
    iget-object v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    const/4 v1, 0x6

    if-ne v0, v1, :cond_1

    iget-boolean v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->hasIcons:Z

    if-eqz v0, :cond_1

    .line 145
    iput-boolean v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    .line 147
    :cond_1
    return-void
.end method

.method public getCurrentTab()Lit/neokree/materialtabs/MaterialTab;
    .locals 3

    .prologue
    .line 311
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-nez v2, :cond_1

    .line 316
    const/4 v0, 0x0

    :goto_0
    return-object v0

    .line 311
    :cond_1
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lit/neokree/materialtabs/MaterialTab;

    .line 312
    .local v0, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v0}, Lit/neokree/materialtabs/MaterialTab;->isSelected()Z

    move-result v2

    if-eqz v2, :cond_0

    goto :goto_0
.end method

.method public newTab()Lit/neokree/materialtabs/MaterialTab;
    .locals 3

    .prologue
    .line 150
    new-instance v0, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getContext()Landroid/content/Context;

    move-result-object v1

    iget-boolean v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->hasIcons:Z

    invoke-direct {v0, v1, v2}, Lit/neokree/materialtabs/MaterialTab;-><init>(Landroid/content/Context;Z)V

    return-object v0
.end method

.method public notifyDataSetChanged()V
    .locals 13

    .prologue
    .line 213
    invoke-super {p0}, Landroid/widget/RelativeLayout;->removeAllViews()V

    .line 214
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v10}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 217
    iget-boolean v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    if-nez v10, :cond_2

    .line 218
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getWidth()I

    move-result v10

    iget-object v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v11}, Ljava/util/List;->size()I

    move-result v11

    div-int v8, v10, v11

    .line 221
    .local v8, "tabWidth":I
    new-instance v1, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v10, -0x1

    invoke-direct {v1, v8, v10}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 222
    .local v1, "params":Landroid/widget/LinearLayout$LayoutParams;
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v10

    :goto_0
    invoke-interface {v10}, Ljava/util/Iterator;->hasNext()Z

    move-result v11

    if-nez v11, :cond_1

    .line 267
    .end local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    .end local v8    # "tabWidth":I
    :cond_0
    iget-boolean v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->isTablet:Z

    if-eqz v10, :cond_6

    iget-boolean v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    if-eqz v10, :cond_6

    .line 269
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getResources()Landroid/content/res/Resources;

    move-result-object v5

    .line 271
    .local v5, "res":Landroid/content/res/Resources;
    new-instance v10, Landroid/widget/ImageButton;

    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getContext()Landroid/content/Context;

    move-result-object v11

    invoke-direct {v10, v11}, Landroid/widget/ImageButton;-><init>(Landroid/content/Context;)V

    iput-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    .line 272
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    sget v11, Lit/neokree/materialtabs/R$id;->left:I

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setId(I)V

    .line 273
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    sget v11, Lit/neokree/materialtabs/R$drawable;->left_arrow:I

    invoke-virtual {v5, v11}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v11

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 274
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    const/4 v11, 0x0

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setBackgroundColor(I)V

    .line 275
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    invoke-virtual {v10, p0}, Landroid/widget/ImageButton;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 278
    new-instance v2, Landroid/widget/RelativeLayout$LayoutParams;

    const/high16 v10, 0x42600000    # 56.0f

    iget v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v10, v11

    float-to-int v10, v10

    const/high16 v11, 0x42400000    # 48.0f

    iget v12, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v11, v12

    float-to-int v11, v11

    invoke-direct {v2, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    .line 279
    .local v2, "paramsLeft":Landroid/widget/RelativeLayout$LayoutParams;
    const/16 v10, 0x9

    invoke-virtual {v2, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 280
    const/16 v10, 0xa

    invoke-virtual {v2, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 281
    const/16 v10, 0xc

    invoke-virtual {v2, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 282
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->left:Landroid/widget/ImageButton;

    invoke-virtual {p0, v10, v2}, Lit/neokree/materialtabs/MaterialTabHost;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 284
    new-instance v10, Landroid/widget/ImageButton;

    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getContext()Landroid/content/Context;

    move-result-object v11

    invoke-direct {v10, v11}, Landroid/widget/ImageButton;-><init>(Landroid/content/Context;)V

    iput-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    .line 285
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    sget v11, Lit/neokree/materialtabs/R$id;->right:I

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setId(I)V

    .line 286
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    sget v11, Lit/neokree/materialtabs/R$drawable;->right_arrow:I

    invoke-virtual {v5, v11}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v11

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 287
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    const/4 v11, 0x0

    invoke-virtual {v10, v11}, Landroid/widget/ImageButton;->setBackgroundColor(I)V

    .line 288
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    invoke-virtual {v10, p0}, Landroid/widget/ImageButton;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 290
    new-instance v3, Landroid/widget/RelativeLayout$LayoutParams;

    const/high16 v10, 0x42600000    # 56.0f

    iget v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v10, v11

    float-to-int v10, v10

    const/high16 v11, 0x42400000    # 48.0f

    iget v12, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v11, v12

    float-to-int v11, v11

    invoke-direct {v3, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    .line 291
    .local v3, "paramsRight":Landroid/widget/RelativeLayout$LayoutParams;
    const/16 v10, 0xb

    invoke-virtual {v3, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 292
    const/16 v10, 0xa

    invoke-virtual {v3, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 293
    const/16 v10, 0xc

    invoke-virtual {v3, v10}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 294
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->right:Landroid/widget/ImageButton;

    invoke-virtual {p0, v10, v3}, Lit/neokree/materialtabs/MaterialTabHost;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 296
    new-instance v4, Landroid/widget/RelativeLayout$LayoutParams;

    const/4 v10, -0x1

    const/4 v11, -0x1

    invoke-direct {v4, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    .line 297
    .local v4, "paramsScroll":Landroid/widget/RelativeLayout$LayoutParams;
    const/4 v10, 0x0

    sget v11, Lit/neokree/materialtabs/R$id;->right:I

    invoke-virtual {v4, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(II)V

    .line 298
    const/4 v10, 0x1

    sget v11, Lit/neokree/materialtabs/R$id;->left:I

    invoke-virtual {v4, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(II)V

    .line 299
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    invoke-virtual {p0, v10, v4}, Lit/neokree/materialtabs/MaterialTabHost;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 307
    .end local v2    # "paramsLeft":Landroid/widget/RelativeLayout$LayoutParams;
    .end local v3    # "paramsRight":Landroid/widget/RelativeLayout$LayoutParams;
    .end local v5    # "res":Landroid/content/res/Resources;
    :goto_1
    sget v10, Lit/neokree/materialtabs/MaterialTabHost;->tabSelected:I

    invoke-virtual {p0, v10}, Lit/neokree/materialtabs/MaterialTabHost;->setSelectedNavigationItem(I)V

    .line 308
    return-void

    .line 222
    .end local v4    # "paramsScroll":Landroid/widget/RelativeLayout$LayoutParams;
    .restart local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    .restart local v8    # "tabWidth":I
    :cond_1
    invoke-interface {v10}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lit/neokree/materialtabs/MaterialTab;

    .line 223
    .local v6, "t":Lit/neokree/materialtabs/MaterialTab;
    iget-object v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v6}, Lit/neokree/materialtabs/MaterialTab;->getView()Landroid/view/View;

    move-result-object v12

    invoke-virtual {v11, v12, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    goto/16 :goto_0

    .line 228
    .end local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    .end local v6    # "t":Lit/neokree/materialtabs/MaterialTab;
    .end local v8    # "tabWidth":I
    :cond_2
    iget-boolean v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->isTablet:Z

    if-nez v10, :cond_5

    .line 229
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_2
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10}, Ljava/util/List;->size()I

    move-result v10

    if-ge v0, v10, :cond_0

    .line 231
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lit/neokree/materialtabs/MaterialTab;

    .line 233
    .local v7, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v7}, Lit/neokree/materialtabs/MaterialTab;->getTabMinWidth()I

    move-result v10

    int-to-float v10, v10

    const/high16 v11, 0x41c00000    # 24.0f

    iget v12, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v11, v12

    add-float/2addr v10, v11

    float-to-int v8, v10

    .line 235
    .restart local v8    # "tabWidth":I
    if-nez v0, :cond_3

    .line 237
    new-instance v9, Landroid/view/View;

    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v10}, Landroid/widget/LinearLayout;->getContext()Landroid/content/Context;

    move-result-object v10

    invoke-direct {v9, v10}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    .line 238
    .local v9, "view":Landroid/view/View;
    const/high16 v10, 0x42700000    # 60.0f

    iget v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v10, v11

    float-to-int v10, v10

    invoke-virtual {v9, v10}, Landroid/view/View;->setMinimumWidth(I)V

    .line 239
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v10, v9}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 242
    .end local v9    # "view":Landroid/view/View;
    :cond_3
    new-instance v1, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v10, -0x1

    invoke-direct {v1, v8, v10}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 243
    .restart local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v7}, Lit/neokree/materialtabs/MaterialTab;->getView()Landroid/view/View;

    move-result-object v11

    invoke-virtual {v10, v11, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 245
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10}, Ljava/util/List;->size()I

    move-result v10

    add-int/lit8 v10, v10, -0x1

    if-ne v0, v10, :cond_4

    .line 247
    new-instance v9, Landroid/view/View;

    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v10}, Landroid/widget/LinearLayout;->getContext()Landroid/content/Context;

    move-result-object v10

    invoke-direct {v9, v10}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    .line 248
    .restart local v9    # "view":Landroid/view/View;
    const/high16 v10, 0x42700000    # 60.0f

    iget v11, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v10, v11

    float-to-int v10, v10

    invoke-virtual {v9, v10}, Landroid/view/View;->setMinimumWidth(I)V

    .line 249
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v10, v9}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 229
    .end local v9    # "view":Landroid/view/View;
    :cond_4
    add-int/lit8 v0, v0, 0x1

    goto :goto_2

    .line 255
    .end local v0    # "i":I
    .end local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    .end local v7    # "tab":Lit/neokree/materialtabs/MaterialTab;
    .end local v8    # "tabWidth":I
    :cond_5
    const/4 v0, 0x0

    .restart local v0    # "i":I
    :goto_3
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10}, Ljava/util/List;->size()I

    move-result v10

    if-ge v0, v10, :cond_0

    .line 257
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v10, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lit/neokree/materialtabs/MaterialTab;

    .line 259
    .restart local v7    # "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v7}, Lit/neokree/materialtabs/MaterialTab;->getTabMinWidth()I

    move-result v10

    int-to-float v10, v10

    const/high16 v11, 0x42400000    # 48.0f

    iget v12, p0, Lit/neokree/materialtabs/MaterialTabHost;->density:F

    mul-float/2addr v11, v12

    add-float/2addr v10, v11

    float-to-int v8, v10

    .line 261
    .restart local v8    # "tabWidth":I
    new-instance v1, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v10, -0x1

    invoke-direct {v1, v8, v10}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 262
    .restart local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v7}, Lit/neokree/materialtabs/MaterialTab;->getView()Landroid/view/View;

    move-result-object v11

    invoke-virtual {v10, v11, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 255
    add-int/lit8 v0, v0, 0x1

    goto :goto_3

    .line 303
    .end local v0    # "i":I
    .end local v1    # "params":Landroid/widget/LinearLayout$LayoutParams;
    .end local v7    # "tab":Lit/neokree/materialtabs/MaterialTab;
    .end local v8    # "tabWidth":I
    :cond_6
    new-instance v4, Landroid/widget/RelativeLayout$LayoutParams;

    const/4 v10, -0x1

    const/4 v11, -0x1

    invoke-direct {v4, v10, v11}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    .line 304
    .restart local v4    # "paramsScroll":Landroid/widget/RelativeLayout$LayoutParams;
    iget-object v10, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollView:Landroid/widget/HorizontalScrollView;

    invoke-virtual {p0, v10, v4}, Lit/neokree/materialtabs/MaterialTabHost;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    goto/16 :goto_1
.end method

.method public onClick(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .prologue
    .line 321
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getCurrentTab()Lit/neokree/materialtabs/MaterialTab;

    move-result-object v1

    invoke-virtual {v1}, Lit/neokree/materialtabs/MaterialTab;->getPosition()I

    move-result v0

    .line 323
    .local v0, "currentPosition":I
    invoke-virtual {p1}, Landroid/view/View;->getId()I

    move-result v1

    sget v2, Lit/neokree/materialtabs/R$id;->right:I

    if-ne v1, v2, :cond_1

    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    if-ge v0, v1, :cond_1

    .line 324
    add-int/lit8 v0, v0, 0x1

    .line 327
    invoke-virtual {p0, v0}, Lit/neokree/materialtabs/MaterialTabHost;->setSelectedNavigationItem(I)V

    .line 330
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v1}, Lit/neokree/materialtabs/MaterialTab;->getTabListener()Lit/neokree/materialtabs/MaterialTabListener;

    move-result-object v2

    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lit/neokree/materialtabs/MaterialTab;

    invoke-interface {v2, v1}, Lit/neokree/materialtabs/MaterialTabListener;->onTabSelected(Lit/neokree/materialtabs/MaterialTab;)V

    .line 344
    :cond_0
    :goto_0
    return-void

    .line 334
    :cond_1
    invoke-virtual {p1}, Landroid/view/View;->getId()I

    move-result v1

    sget v2, Lit/neokree/materialtabs/R$id;->left:I

    if-ne v1, v2, :cond_0

    if-lez v0, :cond_0

    .line 335
    add-int/lit8 v0, v0, -0x1

    .line 338
    invoke-virtual {p0, v0}, Lit/neokree/materialtabs/MaterialTabHost;->setSelectedNavigationItem(I)V

    .line 340
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v1}, Lit/neokree/materialtabs/MaterialTab;->getTabListener()Lit/neokree/materialtabs/MaterialTabListener;

    move-result-object v2

    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lit/neokree/materialtabs/MaterialTab;

    invoke-interface {v2, v1}, Lit/neokree/materialtabs/MaterialTabListener;->onTabSelected(Lit/neokree/materialtabs/MaterialTab;)V

    goto :goto_0
.end method

.method protected onSizeChanged(IIII)V
    .locals 1
    .param p1, "w"    # I
    .param p2, "h"    # I
    .param p3, "oldw"    # I
    .param p4, "oldh"    # I

    .prologue
    .line 206
    invoke-super {p0, p1, p2, p3, p4}, Landroid/widget/RelativeLayout;->onSizeChanged(IIII)V

    .line 207
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->getWidth()I

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    if-eqz v0, :cond_0

    .line 208
    invoke-virtual {p0}, Lit/neokree/materialtabs/MaterialTabHost;->notifyDataSetChanged()V

    .line 209
    :cond_0
    return-void
.end method

.method public removeAllViews()V
    .locals 2

    .prologue
    .line 197
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    if-lt v0, v1, :cond_0

    .line 200
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->layout:Landroid/widget/LinearLayout;

    invoke-virtual {v1}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 201
    invoke-super {p0}, Landroid/widget/RelativeLayout;->removeAllViews()V

    .line 202
    return-void

    .line 198
    :cond_0
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1, v0}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    .line 197
    add-int/lit8 v0, v0, 0x1

    goto :goto_0
.end method

.method public setAccentColor(I)V
    .locals 3
    .param p1, "color"    # I

    .prologue
    .line 105
    iput p1, p0, Lit/neokree/materialtabs/MaterialTabHost;->accentColor:I

    .line 107
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-nez v2, :cond_0

    .line 110
    return-void

    .line 107
    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lit/neokree/materialtabs/MaterialTab;

    .line 108
    .local v0, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v0, p1}, Lit/neokree/materialtabs/MaterialTab;->setAccentColor(I)V

    goto :goto_0
.end method

.method public setIconColor(I)V
    .locals 3
    .param p1, "color"    # I

    .prologue
    .line 121
    iput p1, p0, Lit/neokree/materialtabs/MaterialTabHost;->iconColor:I

    .line 123
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-nez v2, :cond_0

    .line 126
    return-void

    .line 123
    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lit/neokree/materialtabs/MaterialTab;

    .line 124
    .local v0, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v0, p1}, Lit/neokree/materialtabs/MaterialTab;->setIconColor(I)V

    goto :goto_0
.end method

.method public setPrimaryColor(I)V
    .locals 3
    .param p1, "color"    # I

    .prologue
    .line 95
    iput p1, p0, Lit/neokree/materialtabs/MaterialTabHost;->primaryColor:I

    .line 97
    iget v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->primaryColor:I

    invoke-virtual {p0, v1}, Lit/neokree/materialtabs/MaterialTabHost;->setBackgroundColor(I)V

    .line 99
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-nez v2, :cond_0

    .line 102
    return-void

    .line 99
    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lit/neokree/materialtabs/MaterialTab;

    .line 100
    .local v0, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v0, p1}, Lit/neokree/materialtabs/MaterialTab;->setPrimaryColor(I)V

    goto :goto_0
.end method

.method public setSelectedNavigationItem(I)V
    .locals 4
    .param p1, "position"    # I

    .prologue
    .line 154
    if-ltz p1, :cond_0

    iget-object v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->size()I

    move-result v2

    if-le p1, v2, :cond_1

    .line 155
    :cond_0
    new-instance v2, Ljava/lang/RuntimeException;

    const-string v3, "Index overflow"

    invoke-direct {v2, v3}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 158
    :cond_1
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    iget-object v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->size()I

    move-result v2

    if-lt v0, v2, :cond_3

    .line 170
    iget-boolean v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->scrollable:Z

    if-eqz v2, :cond_2

    .line 171
    invoke-direct {p0, p1}, Lit/neokree/materialtabs/MaterialTabHost;->scrollTo(I)V

    .line 174
    :cond_2
    sput p1, Lit/neokree/materialtabs/MaterialTabHost;->tabSelected:I

    .line 177
    return-void

    .line 159
    :cond_3
    iget-object v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v2, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lit/neokree/materialtabs/MaterialTab;

    .line 161
    .local v1, "tab":Lit/neokree/materialtabs/MaterialTab;
    if-ne v0, p1, :cond_4

    .line 162
    invoke-virtual {v1}, Lit/neokree/materialtabs/MaterialTab;->activateTab()V

    .line 158
    :goto_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 165
    :cond_4
    iget-object v2, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v2, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lit/neokree/materialtabs/MaterialTab;

    invoke-virtual {v2}, Lit/neokree/materialtabs/MaterialTab;->disableTab()V

    goto :goto_1
.end method

.method public setTextColor(I)V
    .locals 3
    .param p1, "color"    # I

    .prologue
    .line 113
    iput p1, p0, Lit/neokree/materialtabs/MaterialTabHost;->textColor:I

    .line 115
    iget-object v1, p0, Lit/neokree/materialtabs/MaterialTabHost;->tabs:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-nez v2, :cond_0

    .line 118
    return-void

    .line 115
    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lit/neokree/materialtabs/MaterialTab;

    .line 116
    .local v0, "tab":Lit/neokree/materialtabs/MaterialTab;
    invoke-virtual {v0, p1}, Lit/neokree/materialtabs/MaterialTab;->setTextColor(I)V

    goto :goto_0
.end method
