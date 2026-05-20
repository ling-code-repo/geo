export default {
  path: "/question",
  redirect: "/question/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "扩展问题",
    rank: 4
  },
  children: [
    {
      path: "/question/index",
      name: "Question",
      component: () => import("@/views/question/index.vue"),
      meta: {
        title: "扩展问题"
      }
    },
  ]
} satisfies RouteConfigsTable;
