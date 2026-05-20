export default {
  path: "/picture",
  redirect: "/picture/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "企业图库",
    rank: 5
  },
  children: [
    {
      path: "/picture/index",
      name: "Picture",
      component: () => import("@/views/file/index.vue"),
      meta: {
        title: "企业图库"
      }
    },
  ]
} satisfies RouteConfigsTable;
