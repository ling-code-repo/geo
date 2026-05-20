export default {
  path: "/publish",
  redirect: "/publish/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "发布任务",
    rank:11
  },
  children: [
    {
      path: "/publish/index",
      name: "Publish",
      component: () => import("@/views/publish/index.vue"),
      meta: {
        title: "发布任务"
      }
    },
  ]
} satisfies RouteConfigsTable;
