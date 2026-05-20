export default {
  path: "/record",
  redirect: "/record/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "发布任务",
    rank:13
  },
  children: [
    {
      path: "/record/index",
      name: "Record",
      component: () => import("@/views/record/index.vue"),
      meta: {
        title: "发布记录"
      }
    },
  ]
} satisfies RouteConfigsTable;
