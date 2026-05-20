export default {
  path: "/creation",
  redirect: "/creation/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "创作管理",
    rank: 8
  },
  children: [
    {
      path: "/creation/index",
      name: "Creation",
      component: () => import("@/views/creation/index.vue"),
      meta: {
        title: "创作管理"
      }
    },
  ]
} satisfies RouteConfigsTable;
