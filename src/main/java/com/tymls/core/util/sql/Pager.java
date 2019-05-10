package com.tymls.core.util.sql;

public class Pager {

  public static int ALL_Data_Page_Limit = -1;
  private int total = 0; // 总记录数
  private int limit = 20; // 每页显示记录数
  private int pages = 1; // 总页数
  private int current = 1; // 当前页

  private int start;

  private boolean isFirstPage = false; // 是否为第一页
  private boolean isLastPage = false; // 是否为最后一页
  private boolean hasPreviousPage = false; // 是否有前一页
  private boolean hasNextPage = false; // 是否有下一页

  private int navigatePages = 8; // 导航页码数
  private int[] navigatePageNumbers; // 所有导航页号

  public Pager() {
    this.current = 1;
    this.total = 0;
    this.pages = 1;
    this.limit = 20;
    this.start = (this.current - 1) * this.limit;
  }

  public Pager(int total, int current) {
    init(total, current, limit);
  }

  /**
   * 直接用请求里面的pageindex和pagesize来构建分页对象
   *
   * @param pageIndex
   * @param pageSize
   */
  public Pager(String pageIndex, String pageSize) {
    if (pageIndex == null || pageIndex.equals("")) { // 如果此字段为空，默认查询第1页
      pageIndex = "1";
    }
    if (pageSize != null && !pageSize.equals("")) { // 如果此字段为空，默认为10条记录
      init(total, Integer.parseInt(pageIndex), Integer.parseInt(pageSize));
    } else {
      init(total, current, limit);
    }
  }

  public Pager(int total, int current, int limit) {
    init(total, current, limit);
  }

  private void init(int total, int current, int limit) {
    // 设置基本参数
    this.total = total;
    this.limit = limit;
    if (this.limit != ALL_Data_Page_Limit) {
      this.start = (current - 1) * limit;
      this.pages = (total - 1) / limit + 1;

      // 根据输入可能错误的当前号码进行自动纠正
      if (current < 1) {
        this.current = 1;
      } else if (current > this.pages) {
        this.current = this.pages;
      } else {
        this.current = current;
      }
    } else {
      this.start = 1;
      this.pages = 1;
    }
    // 基本参数设定之后进行导航页面的计算
    calcNavigatePageNumbers();

    // 以及页面边界的判定
    judgePageBoudary();
  }

  /** 计算导航页 */
  private void calcNavigatePageNumbers() {
    if (this.limit != ALL_Data_Page_Limit) {
      // 当总页数小于或等于导航页码数时
      if (pages <= navigatePages) {
        navigatePageNumbers = new int[pages];
        for (int i = 0; i < pages; i++) {
          navigatePageNumbers[i] = i + 1;
        }
      } else { // 当总页数大于导航页码数时
        navigatePageNumbers = new int[navigatePages];
        int startNum = current - navigatePages / 2;
        int endNum = current + navigatePages / 2;

        if (startNum < 1) {
          startNum = 1;
          // (最前navPageCount页
          for (int i = 0; i < navigatePages; i++) {
            navigatePageNumbers[i] = startNum++;
          }
        } else if (endNum > pages) {
          endNum = pages;
          // 最后navPageCount页
          for (int i = navigatePages - 1; i >= 0; i--) {
            navigatePageNumbers[i] = endNum--;
          }
        } else {
          // 所有中间页
          for (int i = 0; i < navigatePages; i++) {
            navigatePageNumbers[i] = startNum++;
          }
        }
      }
    } else {
      navigatePageNumbers = new int[1];
      navigatePageNumbers[0] = 1;
    }
  }

  /** 判定页面边界 */
  private void judgePageBoudary() {
    if (this.limit != ALL_Data_Page_Limit) {
      isFirstPage = current == 1;
      isLastPage = current == pages && current != 1;
      hasPreviousPage = current != 1;
      hasNextPage = current != pages;
    } else {
      isFirstPage = true;
      isLastPage = true;
      hasPreviousPage = false;
      hasNextPage = false;
    }
  }

  /**
   * 得到记录总数
   *
   * @return {int}
   */
  public int getTotal() {
    return total;
  }

  /**
   * 得到每页显示多少条记录
   *
   * @return {int}
   */
  public int getLimit() {
    return limit;
  }

  public boolean isNoLimit() {
    return this.limit == ALL_Data_Page_Limit;
  }
  /**
   * 得到页面总数
   *
   * @return {int}
   */
  public int getPages() {
    return pages;
  }

  /**
   * 得到当前页号
   *
   * @return {int}
   */
  public int getCurrent() {
    return current;
  }

  public int getStart() {
    return start;
  }

  /**
   * 得到所有导航页号
   *
   * @return {int[]}
   */
  public int[] getNavigatePageNumbers() {
    return navigatePageNumbers;
  }

  public boolean isFirstPage() {
    return isFirstPage;
  }

  public boolean isLastPage() {
    return isLastPage;
  }

  public boolean hasPreviousPage() {
    return hasPreviousPage;
  }

  public boolean hasNextPage() {
    return hasNextPage;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void setLimit(int limit) {
    this.limit = limit;
    this.start = (this.current - 1) * limit;
  }

  public void setCurrent(int current) {
    this.current = current;
    this.start = (current - 1) * this.limit;
  }

  public Pager getPager(int start, int limit) {
    this.limit = limit;
    this.start = start;
    return this;
  }
}
