export default {
  path: "/article",
  redirect: "/article/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "创作文章",
    rank:9
  },
  children: [
    {
      path: "/article/index",
      name: "Article",
      component: () => import("@/views/article/index.vue"),
      meta: {
        title: "创作文章"
      }
    },
  ]
} satisfies RouteConfigsTable;
