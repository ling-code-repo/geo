export default {
  path: "/word",
  redirect: "/word/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "蒸馏词",
    rank: 3
  },
  children: [
    {
      path: "/word/index",
      name: "Word",
      component: () => import("@/views/word/index.vue"),
      meta: {
        title: "蒸馏词"
      }
    },
  ]
} satisfies RouteConfigsTable;
